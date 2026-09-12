package com.exercise.WalletTransfers.logging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomainEvent {

	Instant timestamp;

	@JsonProperty("correlation_id")
	String correlationId;

	String event;

	@JsonProperty("unique_reference")
	String uniqueReference;

	@JsonProperty("from_username")
	String fromUsername;

	@JsonProperty("to_username")
	String toUsername;

	@JsonProperty("wallet_id")
	Long walletId;

	@JsonProperty("amount_paise")
	Long amountPaise;

	String status;
	String message;
}
