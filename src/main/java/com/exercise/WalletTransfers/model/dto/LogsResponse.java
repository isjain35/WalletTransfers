package com.exercise.WalletTransfers.model.dto;

import com.exercise.WalletTransfers.logging.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LogsResponse {
	private List<DomainEvent> events;
}
