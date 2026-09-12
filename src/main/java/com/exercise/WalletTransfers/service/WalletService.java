package com.exercise.WalletTransfers.service;

import com.exercise.WalletTransfers.model.dto.ResponseDTO;
import com.exercise.WalletTransfers.model.dto.WalletResponse;
import com.exercise.WalletTransfers.model.postgres.User;
import com.exercise.WalletTransfers.model.postgres.Wallet;
import com.exercise.WalletTransfers.repository.WalletRepository;
import com.exercise.WalletTransfers.utils.RandomStringGenerator;
import com.exercise.WalletTransfers.utils.ResponseMessages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WalletService {

	@Autowired
	WalletRepository walletRepository;

	public ResponseDTO createWallet(User user) {
		ResponseDTO responseDTO = new ResponseDTO();
		WalletResponse walletResponse = new WalletResponse();
		Wallet wallet = new Wallet();
		wallet.setUser(user);
		wallet.setReferenceId(RandomStringGenerator.getInstance().generateString(20));

		try {
			wallet = walletRepository.save(wallet);
			walletResponse.setBalance(wallet.getBalance());
			responseDTO.setHttpStatus(HttpStatus.CREATED);
		} catch (DataIntegrityViolationException e) {
			log.error("Wallet already exists for user: {}", user.getUsername());
			wallet = walletRepository.getByUserId(user.getId());
			walletResponse.setMessage(ResponseMessages.WALLET_ALREADY_EXISTS);
			responseDTO.setHttpStatus(HttpStatus.CONFLICT);
		}
		walletResponse.setWalletId(wallet.getReferenceId());
		responseDTO.setResponseObject(walletResponse);

		return responseDTO;
	}

	public ResponseDTO getWallet(String id, User user) {
		ResponseDTO responseDTO = new ResponseDTO();
		WalletResponse walletResponse = new WalletResponse();

		Wallet wallet = walletRepository.getByUserAndReferenceId(user, id);
		if (wallet != null) {
			walletResponse.setBalance(wallet.getBalance());
			responseDTO.setHttpStatus(HttpStatus.OK);
		} else {
			log.error("Transfer not found for id: {} and user: {}", id, user.getUsername());
			walletResponse.setMessage(ResponseMessages.WALLET_NOT_FOUND);
			responseDTO.setHttpStatus(HttpStatus.NOT_FOUND);
		}
		responseDTO.setResponseObject(walletResponse);

		return responseDTO;
	}
}
