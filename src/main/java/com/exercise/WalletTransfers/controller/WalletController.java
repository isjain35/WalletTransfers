package com.exercise.WalletTransfers.controller;

import com.exercise.WalletTransfers.config.UsersDetails;
import com.exercise.WalletTransfers.model.postgres.User;
import com.exercise.WalletTransfers.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

	@Autowired
	WalletService walletService;

	@PostMapping
	public ResponseEntity<Object> createWallet(@AuthenticationPrincipal UsersDetails usersDetails) {
		return walletService.createWallet(usersDetails.getUser()).getResponseEntity();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getWallet(@PathVariable String id, @AuthenticationPrincipal UsersDetails usersDetails) {
		return walletService.getWallet(id, usersDetails.getUser()).getResponseEntity();
	}
}
