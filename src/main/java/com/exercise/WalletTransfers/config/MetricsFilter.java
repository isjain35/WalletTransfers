package com.exercise.WalletTransfers.config;

import com.exercise.WalletTransfers.metrics.WalletMetrics;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class MetricsFilter extends OncePerRequestFilter {

	private final WalletMetrics walletMetrics;

	public MetricsFilter(WalletMetrics walletMetrics) {
		this.walletMetrics = walletMetrics;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return "/metrics".equals(path) || "/metrics.html".equals(path);
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		long start = System.nanoTime();
		try {
			filterChain.doFilter(request, response);
		} finally {
			walletMetrics.recordHttp(System.nanoTime() - start, response.getStatus() >= 500);
		}
	}
}
