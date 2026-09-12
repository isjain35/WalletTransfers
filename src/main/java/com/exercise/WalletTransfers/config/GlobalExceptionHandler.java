package com.exercise.WalletTransfers.config;

import com.exercise.WalletTransfers.model.exception.WalletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(WalletException.class)
	public ResponseEntity<Object> handleWalletException(WalletException e) {
		log.error("WalletException occurred: ", e);
		return new ResponseEntity<>(e.getResponseObject(), e.getHttpStatus());
	}
}
