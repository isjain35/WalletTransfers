package com.exercise.WalletTransfers.controller;

import com.exercise.WalletTransfers.logging.DomainEventLogger;
import com.exercise.WalletTransfers.model.dto.LogsResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogsController {

	private final DomainEventLogger domainEventLogger;

	public LogsController(DomainEventLogger domainEventLogger) {
		this.domainEventLogger = domainEventLogger;
	}

	@GetMapping
	public LogsResponse getLogs(@RequestParam(required = false) String correlationId) {
		return new LogsResponse(domainEventLogger.recent(correlationId));
	}
}
