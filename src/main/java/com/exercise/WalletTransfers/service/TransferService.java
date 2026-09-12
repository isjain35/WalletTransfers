package com.exercise.WalletTransfers.service;

import com.exercise.WalletTransfers.dao.WalletDao;
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

	@Transactional
	public ResponseDTO createTransfer(TransferRequest request, User fromUser) {
		ResponseDTO responseDTO = new ResponseDTO();
		TransferResponse transferResponse = transferAmount(request, fromUser.getId());
		responseDTO.setResponseObject(transferResponse);
		responseDTO.setHttpStatus(HttpStatus.OK);
		return responseDTO;
	}

	private TransferResponse transferAmount(TransferRequest request, Long senderId) {
		TransferResponse transferResponse = new TransferResponse();
		Transaction transaction = new Transaction();
		Wallet senderWallet = walletRepository.getByUserId(senderId);
		Wallet receiverWallet = walletRepository.getByUserUsername(request.getSendTo());

		if (senderWallet == null) {
			transferResponse.setMessage(ResponseMessages.TRANSFER_FAILED_SENDER_WALLET_NOT_REGISTERED);
			throw new WalletException(HttpStatus.BAD_REQUEST, transferResponse);
		}
		if (receiverWallet == null) {
			transferResponse.setMessage(ResponseMessages.TRANSFER_FAILED_RECIPIENT_WALLET_NOT_REGISTERED);
			throw new WalletException(HttpStatus.BAD_REQUEST, transferResponse);
		}

		int debitWalletCount = walletDao.debitWallet(senderWallet.getId(), request.getAmountPaise());
		if (debitWalletCount != 0) {
			int creditWalletCount = walletDao.creditWallet(receiverWallet.getId(), request.getAmountPaise());
			if (creditWalletCount != 0) {
				transaction.setStatus(TransferStatus.SUCCESSFUL);
				transaction.setMessage(ResponseMessages.TRANSFER_SUCCESSFUL);
				transferResponse.setStatus(transaction.getStatus());
				transferResponse.setMessage(transaction.getMessage());
			} else {
				transferResponse.setMessage(ResponseMessages.TRANSFER_FAILED_RECIPIENT_WALLET_NOT_REGISTERED);
				throw new WalletException(HttpStatus.BAD_REQUEST, transferResponse);
			}
		} else {
			transaction.setStatus(TransferStatus.FAILED);
			transaction.setMessage(ResponseMessages.TRANSFER_FAILED_INSUFFICIENT_BALANCE);
			transferResponse.setStatus(transaction.getStatus());
			transferResponse.setMessage(transaction.getMessage());
		}

		try {
			transaction.setUniqueReference(request.getUniqueReference());
			transaction.setAmountPaise(request.getAmountPaise());
			transaction.setFromWallet(senderWallet);
			transaction.setToWallet(receiverWallet);
			transactionRepository.save(transaction);
		} catch (DataIntegrityViolationException e) {
			log.error("Transfer already exists for unique_reference: {} for userId: {}", request.getUniqueReference(), senderId);
			transactionRepository.getByUniqueReferenceAndFromWalletUserId(request.getUniqueReference(), senderId);
			transferResponse.setSentTo(transaction.getToWallet().getUser().getUsername());
			transferResponse.setAmountPaise(transaction.getAmountPaise());
			transferResponse.setStatus(transaction.getStatus());
			transferResponse.setMessage(transaction.getMessage());
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
}
