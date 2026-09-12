package com.exercise.WalletTransfers.controller;

import com.exercise.WalletTransfers.config.UsersDetails;
import com.exercise.WalletTransfers.model.dto.TransferRequest;
import com.exercise.WalletTransfers.service.TransferService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transfers")
public class TransferController {

	@Autowired
	TransferService transferService;

	@PostMapping
	public ResponseEntity<Object> createTransfer(
			@Valid @RequestBody TransferRequest request,
			@AuthenticationPrincipal UsersDetails usersDetails) {
		return transferService.createTransfer(request, usersDetails.getUser()).getResponseEntity();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> getTransfer(
			@PathVariable String id, @AuthenticationPrincipal UsersDetails usersDetails) {
		return transferService.getTransfer(id, usersDetails.getUser()).getResponseEntity();
	}
}
