package com.exercise.WalletTransfers.controller;

import com.exercise.WalletTransfers.model.dto.AuthRequest;
import com.exercise.WalletTransfers.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@Valid @RequestBody AuthRequest request) {
		return authService.signup(request).getResponseEntity();
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody AuthRequest request) {
		return authService.login(request).getResponseEntity();
	}
}
