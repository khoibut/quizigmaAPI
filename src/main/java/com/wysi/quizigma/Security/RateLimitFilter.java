package com.wysi.quizigma.Security;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

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

    // Store a bucket for each client
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Set the rate limit parameters
    private static final int REQUESTS_PER_PERIOD = 5; // 5 requests
    private static final int PERIOD_SECONDS = 10; // per 10 seconds
    private static final int MAX_BUCKET_LIFETIME = 60; // Time in seconds to clean up unused buckets

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String clientId = request.getRemoteAddr(); // or you could use another identifier like API key

        // Create or get the existing bucket for the client
        Bucket bucket = buckets.computeIfAbsent(clientId, k -> createNewBucket());

        // Check if the client is allowed to make a request
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Allow request if rate limit is not exceeded
            filterChain.doFilter(request, response);
        } else {
            // Reject request if rate limit is exceeded
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Rate limit exceeded. Try again later.\"}");
        }
    }

    // Create a new Bucket with specific rate limits
    private Bucket createNewBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.simple(REQUESTS_PER_PERIOD, Duration.ofSeconds(PERIOD_SECONDS)))
                .build();
    }

    // Optionally, add a method to clean up unused buckets
    public void cleanupBuckets() {
        long currentTime = System.currentTimeMillis();
        buckets.entrySet().removeIf(entry -> (currentTime - entry.getValue().getAvailableTokens()) > MAX_BUCKET_LIFETIME * 1000);
    }
}
