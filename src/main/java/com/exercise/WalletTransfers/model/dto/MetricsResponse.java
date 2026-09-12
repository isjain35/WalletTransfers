package com.exercise.WalletTransfers.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetricsResponse {
	private double requestRatePerSec;
	private double latencyP99Ms;
	private double errorRate;
	private long transfersCreated;
	private long declinedInsufficientFunds;
	private long idempotentReplays;
}
