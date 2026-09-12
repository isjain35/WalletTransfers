package com.exercise.WalletTransfers.service;

import com.exercise.WalletTransfers.dao.WalletDao;
import com.exercise.WalletTransfers.logging.DomainEvent;
import com.exercise.WalletTransfers.logging.DomainEventLogger;
import com.exercise.WalletTransfers.metrics.WalletMetrics;
import com.exercise.WalletTransfers.model.dto.ResponseDTO;
import com.exercise.WalletTransfers.model.dto.TransferRequest;
import com.exercise.WalletTransfers.model.dto.TransferResponse;
import com.exercise.WalletTransfers.model.exception.WalletException;
import com.exercise.WalletTransfers.model.postgres.Transaction;
import com.exercise.WalletTransfers.model.postgres.User;
import com.exercise.WalletTransfers.model.postgres.Wallet;
import com.exercise.WalletTransfers.repository.TransactionRepository;
import com.exercise.WalletTransfers.repository.WalletRepository;
import com.exercise.WalletTransfers.utils.ResponseMessages;
import com.exercise.WalletTransfers.utils.TransferStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TransferService {

	@Autowired
	WalletRepository walletRepository;
	@Autowired
	WalletDao walletDao;
	@Autowired
	TransactionRepository transactionRepository;
	@Autowired
	DomainEventLogger domainEventLogger;
	@Autowired
	TransactionSaveHelper transactionSaveHelper;
	@Autowired
	WalletMetrics walletMetrics;

	@Transactional
	public ResponseDTO createTransfer(TransferRequest request, User fromUser) {
		ResponseDTO responseDTO = new ResponseDTO();
		TransferResponse transferResponse = transferAmount(request, fromUser.getId(), fromUser.getUsername());
		responseDTO.setResponseObject(transferResponse);
		responseDTO.setHttpStatus(HttpStatus.OK);
		return responseDTO;
	}

	private TransferResponse transferAmount(TransferRequest request, Long senderId, String senderUsername) {
		TransferResponse transferResponse = new TransferResponse();
		Transaction transaction = new Transaction();

		logTransfer(DomainEventLogger.TRANSFER_CREATED, request, senderUsername);

		Wallet senderWallet = walletRepository.getByUserId(senderId);
		Wallet receiverWallet = walletRepository.getByUserUsername(request.getSendTo());

		if (senderWallet == null) {
			transferResponse.setMessage(ResponseMessages.TRANSFER_FAILED_SENDER_WALLET_NOT_REGISTERED);
			logDeclined(request, senderUsername, null, null, transferResponse.getMessage());
			throw new WalletException(HttpStatus.BAD_REQUEST, transferResponse);
		}
		if (receiverWallet == null) {
			transferResponse.setMessage(ResponseMessages.TRANSFER_FAILED_RECIPIENT_WALLET_NOT_REGISTERED);
			logDeclined(request, senderUsername, senderWallet.getId(), request.getSendTo(), transferResponse.getMessage());
			throw new WalletException(HttpStatus.BAD_REQUEST, transferResponse);
		}

		int debitWalletCount = walletDao.debitWallet(senderWallet.getId(), request.getAmountPaise());
		if (debitWalletCount != 0) {
			logWalletEntry(DomainEventLogger.MARKED_FOR_DEBIT, request, senderUsername, senderWallet.getId());
			int creditWalletCount = walletDao.creditWallet(receiverWallet.getId(), request.getAmountPaise());

			if (creditWalletCount != 0) {
				logWalletEntry(DomainEventLogger.MARKED_FOR_CREDIT, request, senderUsername, receiverWallet.getId());
				transaction.setStatus(TransferStatus.SUCCESSFUL);
				transaction.setMessage(ResponseMessages.TRANSFER_SUCCESSFUL);
				transferResponse.setStatus(transaction.getStatus());
				transferResponse.setMessage(transaction.getMessage());
			} else {
				transferResponse.setMessage(ResponseMessages.TRANSFER_FAILED_RECIPIENT_WALLET_NOT_REGISTERED);
				logDeclined(request, senderUsername, receiverWallet.getId(), request.getSendTo(), transferResponse.getMessage());
				throw new WalletException(HttpStatus.BAD_REQUEST, transferResponse);
			}
		} else {
			transaction.setStatus(TransferStatus.FAILED);
			transaction.setMessage(ResponseMessages.TRANSFER_FAILED_INSUFFICIENT_BALANCE);
			transferResponse.setStatus(transaction.getStatus());
			transferResponse.setMessage(transaction.getMessage());
			walletMetrics.incrementDeclinedInsufficientFunds();
			logDeclined(request, senderUsername, senderWallet.getId(), request.getSendTo(), ResponseMessages.TRANSFER_FAILED_INSUFFICIENT_BALANCE);
		}

		try {
			transaction.setUniqueReference(request.getUniqueReference());
			transaction.setAmountPaise(request.getAmountPaise());
			transaction.setFromWallet(senderWallet);
			transaction.setToWallet(receiverWallet);
			transactionSaveHelper.saveAndFlushIsolated(transaction);
			walletMetrics.incrementTransfersCreated();
			logTransfer(DomainEventLogger.TRANSFER_COMPLETED, request, senderUsername);
		} catch (DataIntegrityViolationException e) {
			log.error("Transfer already exists for unique_reference: {} for userId: {}", request.getUniqueReference(), senderId);

			Transaction existingTransaction = transactionRepository.getByUniqueReferenceAndFromWalletUserId(request.getUniqueReference(), senderId);
			transferResponse.setSentTo(existingTransaction.getToWallet().getUser().getUsername());
			transferResponse.setAmountPaise(existingTransaction.getAmountPaise());
			transferResponse.setStatus(existingTransaction.getStatus());
			transferResponse.setMessage(existingTransaction.getMessage());
			walletMetrics.incrementIdempotentReplays();
			logIdempotentReplay(transaction, senderUsername, request);
			throw new WalletException(HttpStatus.CONFLICT, transferResponse);
		}
		return transferResponse;
	}

	public ResponseDTO getTransfer(String id, User fromUser) {
		ResponseDTO responseDTO = new ResponseDTO();
		TransferResponse transferResponse = new TransferResponse();

		Transaction transaction = transactionRepository.getByUniqueReferenceAndFromWalletUserId(id, fromUser.getId());
		if (transaction != null) {
			transferResponse.setSentTo(transaction.getToWallet().getUser().getUsername());
			transferResponse.setAmountPaise(transaction.getAmountPaise());
			transferResponse.setStatus(transaction.getStatus());
			transferResponse.setMessage(transaction.getMessage());
			responseDTO.setHttpStatus(HttpStatus.OK);
		} else {
			log.error("Transfer not found for id: {} and user: {}", id, fromUser.getUsername());
			transferResponse.setMessage(ResponseMessages.TRANSFER_NOT_FOUND);
			responseDTO.setHttpStatus(HttpStatus.NOT_FOUND);
		}
		responseDTO.setResponseObject(transferResponse);

		return responseDTO;
	}

	private void logIdempotentReplay(Transaction existing, String fromUsername, TransferRequest request) {
		domainEventLogger.emit(
				DomainEventLogger.IDEMPOTENT_REPLAY_HIT,
				DomainEvent.builder()
						.uniqueReference(request.getUniqueReference())
						.fromUsername(fromUsername)
						.toUsername(existing.getToWallet().getUser().getUsername())
						.amountPaise(existing.getAmountPaise())
						.status(existing.getStatus())
						.message(existing.getMessage()));
	}

	private void logDeclined(TransferRequest request, String fromUsername, Long walletId, String toUsername, String message) {
		domainEventLogger.emit(
				DomainEventLogger.DECLINED,
				DomainEvent.builder()
						.uniqueReference(request.getUniqueReference())
						.fromUsername(fromUsername)
						.toUsername(toUsername)
						.walletId(walletId)
						.amountPaise(request.getAmountPaise())
						.status(TransferStatus.FAILED)
						.message(message));
	}

	private void logWalletEntry(String event, TransferRequest request, String fromUsername, Long walletId) {
		domainEventLogger.emit(
				event,
				DomainEvent.builder()
						.uniqueReference(request.getUniqueReference())
						.fromUsername(fromUsername)
						.toUsername(request.getSendTo())
						.walletId(walletId)
						.amountPaise(request.getAmountPaise()));
	}

	private void logTransfer(String event, TransferRequest request, String fromUsername) {
		domainEventLogger.emit(
				event,
				DomainEvent.builder()
						.uniqueReference(request.getUniqueReference())
						.fromUsername(fromUsername)
						.toUsername(request.getSendTo())
						.amountPaise(request.getAmountPaise()));
	}
}
