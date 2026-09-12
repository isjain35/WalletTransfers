package com.exercise.WalletTransfers.controller;

import com.exercise.WalletTransfers.metrics.WalletMetrics;
import com.exercise.WalletTransfers.model.dto.MetricsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

	@Autowired
	WalletMetrics walletMetrics;

//	public MetricsController(WalletMetrics walletMetrics) {
//		this.walletMetrics = walletMetrics;
//	}

	@GetMapping
	public MetricsResponse getMetrics() {
		return walletMetrics.snapshot();
	}
}
