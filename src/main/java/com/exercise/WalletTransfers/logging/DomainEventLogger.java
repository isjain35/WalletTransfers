package com.exercise.WalletTransfers.logging;

import com.exercise.WalletTransfers.config.CorrelationIdFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class DomainEventLogger {

	public static final String TRANSFER_CREATED = "transfer created";
	public static final String TRANSFER_COMPLETED = "transfer completed";
	public static final String MARKED_FOR_DEBIT = "marked for debit";
	public static final String MARKED_FOR_CREDIT = "marked for credit";
	public static final String DECLINED = "declined";
	public static final String IDEMPOTENT_REPLAY_HIT = "idempotent replay hit";

	private static final int MAX_EVENTS = 200;
	private static final Logger log = LoggerFactory.getLogger("wallet.events");

	private final ConcurrentLinkedDeque<DomainEvent> events = new ConcurrentLinkedDeque<>();

	public void emit(String event, DomainEvent.DomainEventBuilder builder) {
		DomainEvent domainEvent = builder
				.event(event)
				.correlationId(MDC.get(CorrelationIdFilter.MDC_KEY))
				.timestamp(Instant.now())
				.build();
		events.addLast(domainEvent);
		while (events.size() > MAX_EVENTS) {
			events.pollFirst();
		}
		log.atInfo()
				.addKeyValue("event", domainEvent.getEvent())
				.addKeyValue("correlationId", domainEvent.getCorrelationId())
				.addKeyValue("uniqueReference", domainEvent.getUniqueReference())
				.addKeyValue("fromUserId", domainEvent.getFromUsername())
				.addKeyValue("toUsername", domainEvent.getToUsername())
				.addKeyValue("walletId", domainEvent.getWalletId())
				.addKeyValue("amountPaise", domainEvent.getAmountPaise())
				.addKeyValue("status", domainEvent.getStatus())
				.addKeyValue("message", domainEvent.getMessage())
				.log(domainEvent.getEvent());
	}

	public List<DomainEvent> recent(String correlationId) {
		List<DomainEvent> snapshot = new ArrayList<>(events);
		if (correlationId == null || correlationId.isBlank()) {
			return snapshot;
		}
		return snapshot.stream()
				.filter(event -> correlationId.equals(event.getCorrelationId()))
				.toList();
	}
}
