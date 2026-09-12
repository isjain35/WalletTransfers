package com.exercise.WalletTransfers.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferRequest {

	@JsonProperty("send_to")
	private String sendTo;

	@JsonProperty("amount_paise")
	private Long amountPaise;

	@JsonProperty("unique_reference")
	private String uniqueReference;
}
