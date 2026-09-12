package com.exercise.WalletTransfers.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
	@NotBlank(message = "sendTo cannot be blank")
	private String sendTo;

	@JsonProperty("amount_paise")
	@Min(1)
	private Long amountPaise;

	@JsonProperty("unique_reference")
	@NotBlank(message = "uniqueReference cannot be blank")
	private String uniqueReference;
}
