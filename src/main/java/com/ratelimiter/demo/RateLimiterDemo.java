package com.ratelimiter.demo;

import com.ratelimiter.core.RateLimiter;
import com.ratelimiter.core.RateLimiterConfig;
import com.ratelimiter.core.RateLimiterFactory;
import com.ratelimiter.core.RateLimiterFactory.Algorithm;

import java.time.Duration;

public class RateLimiterDemo {

    private static final int LIMIT = 5;
    private static final Duration WINDOW = Duration.ofSeconds(1);

    public static void main(String[] args) throws InterruptedException {
        RateLimiterConfig config = RateLimiterConfig.builder()
                .maxRequests(LIMIT)
                .windowDuration(WINDOW)
                .build();

        System.out.println("=== Rate Limiter Demo ===");
        System.out.printf("Config: %d requests per %dms%n%n", LIMIT, WINDOW.toMillis());

        for (Algorithm algorithm : Algorithm.values()) {
            RateLimiter limiter = RateLimiterFactory.create(algorithm, config);
            runDemo(limiter);
            Thread.sleep(100);
        }
    }

    private static void runDemo(RateLimiter limiter) {
        System.out.println("--- " + limiter.describe() + " ---");

        // Fire 8 requests in rapid succession — expect 5 allowed, 3 denied
        int allowed = 0;
        int denied = 0;
        for (int i = 1; i <= 8; i++) {
            boolean result = limiter.tryAcquire();
            System.out.printf("  Request %d: %s%n", i, result ? "ALLOWED" : "DENIED ");
            if (result) allowed++; else denied++;
        }
        System.out.printf("  Result: %d allowed, %d denied%n%n", allowed, denied);
    }
}
