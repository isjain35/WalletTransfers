package com.exercise.WalletTransfers.model.dto;

import com.exercise.WalletTransfers.utils.TransferStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponse {

	private String message;
	private String status;

	@JsonProperty("sent_to")
	private String sentTo;

	private Long amountPaise;
}
