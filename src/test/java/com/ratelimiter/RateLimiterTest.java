package com.ratelimiter;

import com.ratelimiter.core.RateLimiter;
import com.ratelimiter.core.RateLimiterConfig;
import com.ratelimiter.core.RateLimiterFactory;
import com.ratelimiter.core.RateLimiterFactory.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest {

    private RateLimiter build(Algorithm algorithm, int max, Duration window) {
        RateLimiterConfig config = RateLimiterConfig.builder()
                .maxRequests(max)
                .windowDuration(window)
                .build();
        return RateLimiterFactory.create(algorithm, config);
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    @DisplayName("allows exactly maxRequests within the window")
    void allowsExactLimit(Algorithm algorithm) {
        RateLimiter limiter = build(algorithm, 5, Duration.ofSeconds(10));

        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.tryAcquire(), "request " + (i + 1) + " should be allowed");
        }
        assertFalse(limiter.tryAcquire(), "6th request should be denied");
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    @DisplayName("denies all requests when limit is 1 and bucket is consumed")
    void singleRequestLimit(Algorithm algorithm) {
        RateLimiter limiter = build(algorithm, 1, Duration.ofSeconds(10));

        assertTrue(limiter.tryAcquire(), "first request should pass");
        assertFalse(limiter.tryAcquire(), "second request should be denied");
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    @DisplayName("recovers after the window elapses")
    void recoversAfterWindow(Algorithm algorithm) throws InterruptedException {
        RateLimiter limiter = build(algorithm, 3, Duration.ofMillis(100));

        for (int i = 0; i < 3; i++) limiter.tryAcquire();
        assertFalse(limiter.tryAcquire(), "should be denied before window resets");

        Thread.sleep(150);
        assertTrue(limiter.tryAcquire(), "should be allowed after window resets");
    }

    @ParameterizedTest
    @EnumSource(Algorithm.class)
    @DisplayName("describe() returns a non-empty string")
    void describeIsNonEmpty(Algorithm algorithm) {
        RateLimiter limiter = build(algorithm, 10, Duration.ofSeconds(1));
        assertNotNull(limiter.describe());
        assertFalse(limiter.describe().isBlank());
    }
}
