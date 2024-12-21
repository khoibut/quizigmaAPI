package com.wysi.quizigma.security;

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
    private final static Logger rateLimitLogger = LoggerFactory.getLogger(RateLimitFilter.class);
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final int REQUESTS_PER_PERIOD = 5;
    private static final int PERIOD_SECONDS = 10;
    private static final int MAX_BUCKET_LIFETIME = 60;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Skip rate limiting for Swagger-related URLs
        if (request.getRequestURI().startsWith("/swagger-ui") || request.getRequestURI().startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response); // Allow the request to proceed without rate limiting
            return;
        }

        String clientId = request.getRemoteAddr();

        // Retrieve or create a rate limiting bucket for the client
        Bucket bucket = buckets.computeIfAbsent(clientId, k -> createNewBucket());

        // Try to consume a token from the bucket
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        // If the rate limit is not exceeded, allow the request
        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
        } else {
            // If rate limit is exceeded, return a 429 error
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Rate limit exceeded. Try again later.\"}");
            rateLimitLogger.warn("Rate limit exceeded for client {}", clientId);
        }
    }

    // Creates a new bucket with the specified rate limit settings
    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(REQUESTS_PER_PERIOD, Duration.ofSeconds(PERIOD_SECONDS)))
                .build();
    }

    // Clean up old buckets that have expired based on MAX_BUCKET_LIFETIME
    public void cleanupBuckets() {
        long currentTime = System.currentTimeMillis();
        buckets.entrySet().removeIf(entry -> (currentTime - entry.getValue().getAvailableTokens()) > MAX_BUCKET_LIFETIME * 1000);
    }
}
