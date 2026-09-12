package com.exercise.WalletTransfers.service;

import com.exercise.WalletTransfers.model.dto.AuthRequest;
import com.exercise.WalletTransfers.model.dto.AuthResponse;
import com.exercise.WalletTransfers.model.dto.ResponseDTO;
import com.exercise.WalletTransfers.model.postgres.User;
import com.exercise.WalletTransfers.repository.UserRepository;
import java.util.UUID;

import com.exercise.WalletTransfers.utils.JwtUtils;
import com.exercise.WalletTransfers.utils.ResponseMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class AuthService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	AuthenticationManager authenticationManager;

	public ResponseDTO signup(AuthRequest request) {
		AuthResponse authResponse = new AuthResponse();
		ResponseDTO responseDTO = new ResponseDTO();
		User user = User.builder()
						.username(request.getUsername())
						.password(getEncryptedPassword(request.getPassword()))
						.build();
		try {
			userRepository.save(user);
			authResponse.setMessage(ResponseMessages.SUCCESSFUL_SIGNUP);
			responseDTO.setHttpStatus(HttpStatus.CREATED);
		} catch (DataIntegrityViolationException e) {
			log.error("Error occurred while signing up user: {}", request.getUsername());
			authResponse.setMessage(ResponseMessages.USER_ALREADY_EXISTS);
			responseDTO.setHttpStatus(HttpStatus.CONFLICT);
		}
		responseDTO.setResponseObject(authResponse);

		return responseDTO;
	}

	public ResponseDTO login(AuthRequest request) {
		ResponseDTO responseDTO = new ResponseDTO();
		AuthResponse authResponse = new AuthResponse();

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getUsername(),
							request.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwtToken = jwtUtils.generateJwtToken(authentication);

			authResponse.setAuthToken(jwtToken);
			authResponse.setMessage(ResponseMessages.LOGIN_SUCCESSFUL);
			responseDTO.setHttpStatus(HttpStatus.OK);
		} catch (BadCredentialsException e) {
			log.error("Wrong credentials are being used for user: {}",request.getUsername());
			authResponse.setMessage(ResponseMessages.LOGIN_FAILED);
			responseDTO.setHttpStatus(HttpStatus.UNAUTHORIZED);
		} finally {
			responseDTO.setResponseObject(authResponse);
		}

		return responseDTO;
	}

	private String getEncryptedPassword(String apiKey) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder.encode(apiKey);
	}
}
