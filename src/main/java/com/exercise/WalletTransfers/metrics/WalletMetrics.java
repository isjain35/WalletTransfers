package com.exercise.WalletTransfers.metrics;

import com.exercise.WalletTransfers.model.dto.MetricsResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

@Component
public class WalletMetrics {

	private static final long WINDOW_MS = 60_000L;

	private final Timer httpTimer;
	private final Counter transfersCreated;
	private final Counter declinedInsufficientFunds;
	private final Counter idempotentReplays;
	private final ConcurrentLinkedDeque<HttpSample> window = new ConcurrentLinkedDeque<>();

	public WalletMetrics(MeterRegistry meterRegistry) {
		this.httpTimer = Timer.builder("wallet.http")
				.publishPercentiles(0.99)
				.register(meterRegistry);
		this.transfersCreated = Counter.builder("wallet.transfers.created")
				.register(meterRegistry);
		this.declinedInsufficientFunds = Counter.builder("wallet.transfers.declined_insufficient_funds")
				.register(meterRegistry);
		this.idempotentReplays = Counter.builder("wallet.transfers.idempotent_replays")
				.register(meterRegistry);
	}

	public void recordHttp(long durationNanos, boolean serverError) {
		httpTimer.record(durationNanos, TimeUnit.NANOSECONDS);
		long now = System.currentTimeMillis();
		window.addLast(new HttpSample(now, durationNanos, serverError));
		prune(now);
	}

	public void incrementTransfersCreated() {
		transfersCreated.increment();
	}

	public void incrementDeclinedInsufficientFunds() {
		declinedInsufficientFunds.increment();
	}

	public void incrementIdempotentReplays() {
		idempotentReplays.increment();
	}

	public MetricsResponse snapshot() {
		List<HttpSample> samples = snapshotWindow();
		int n = samples.size();
		double requestRatePerSec = n / (WINDOW_MS / 1000.0);
		double errorRate = 0.0;
		double latencyP99Ms = 0.0;
		if (n > 0) {
			long errors = samples.stream().filter(HttpSample::serverError).count();
			errorRate = (double) errors / n;
			samples.sort(Comparator.comparingLong(HttpSample::durationNanos));
			int p99Index = Math.min(n - 1, (int) Math.ceil(n * 0.99) - 1);
			latencyP99Ms = samples.get(Math.max(p99Index, 0)).durationNanos() / 1_000_000.0;
		}
		return new MetricsResponse(
				requestRatePerSec,
				latencyP99Ms,
				errorRate,
				(long) transfersCreated.count(),
				(long) declinedInsufficientFunds.count(),
				(long) idempotentReplays.count());
	}

	private List<HttpSample> snapshotWindow() {
		prune(System.currentTimeMillis());
		return new ArrayList<>(window);
	}

	private void prune(long now) {
		while (!window.isEmpty()) {
			HttpSample oldest = window.peekFirst();
			if (oldest == null || now - oldest.epochMillis() <= WINDOW_MS) {
				break;
			}
			window.pollFirst();
		}
	}

	private record HttpSample(long epochMillis, long durationNanos, boolean serverError) {
	}
}
