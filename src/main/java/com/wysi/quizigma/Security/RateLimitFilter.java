package com.wysi.quizigma.Security;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {
    private final static Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final int REQUESTS_PER_PERIOD = 5;
    private static final int PERIOD_SECONDS = 10;
    private static final int MAX_BUCKET_LIFETIME = 60;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String clientId = request.getRemoteAddr();

        Bucket bucket = buckets.computeIfAbsent(clientId, k -> createNewBucket());

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Rate limit exceeded. Try again later.\"}");
            logger.warn("Rate limit exceeded for client {}", clientId);
        }
    }

    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(REQUESTS_PER_PERIOD, Duration.ofSeconds(PERIOD_SECONDS)))
                .build();
    }

    public void cleanupBuckets() {
        long currentTime = System.currentTimeMillis();
        buckets.entrySet().removeIf(entry -> (currentTime - entry.getValue().getAvailableTokens()) > MAX_BUCKET_LIFETIME * 1000);
    }
}
