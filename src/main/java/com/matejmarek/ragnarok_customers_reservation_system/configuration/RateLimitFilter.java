package com.matejmarek.ragnarok_customers_reservation_system.configuration;

import io.github.bucket4j.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitFilter extends OncePerRequestFilter {

    // Jednoduché per-node úložiště bucketů (stačí pro jeden běžící node).
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Default limity – přepíšeš v properties (viz níže).
    private final long capacity = 100;             // max requestů v okně
    private final long refillTokens = 100;         // kolik tokenů doplnit
    private final Duration refillPeriod = Duration.ofMinutes(1); // okno

    private Bucket newBucket() {
        Refill refill = Refill.greedy(refillTokens, refillPeriod);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveClientKey(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            // vezmeme první IP z řetězce
            int idx = xff.indexOf(',');
            return (idx > 0) ? xff.substring(0, idx).trim() : xff.trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String key = resolveClientKey(req);
        Bucket bucket = buckets.computeIfAbsent(key, k -> newBucket());

        // try get 1 token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            long remaining = probe.getRemainingTokens(); // how many tokens are achievable?
            res.setHeader("X-Rate-Limit-Remaining", String.valueOf(remaining));
            chain.doFilter(req, res);
        } else {
            // limit is over - how long need to wait?
            long nanosToRefill = probe.getNanosToWaitForRefill(); // nanosec.
            long seconds = Math.max(1, nanosToRefill / 1_000_000_000L);

            res.setStatus(429); // Too Many Requests
            res.setHeader("Retry-After", String.valueOf(seconds));
            res.getWriter().write("Too Many Requests");
        }
    }
}
