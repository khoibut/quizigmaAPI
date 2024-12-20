package com.wysi.quizigma.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RateLimitCleanupTask {

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Scheduled(fixedRate = 60000) // Cleanup every minute
    public void cleanup() {
        rateLimitFilter.cleanupBuckets();
    }
}

