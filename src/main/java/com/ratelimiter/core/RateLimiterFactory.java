package com.ratelimiter.core;

import com.ratelimiter.algorithms.FixedWindowRateLimiter;
import com.ratelimiter.algorithms.LeakyBucketRateLimiter;
import com.ratelimiter.algorithms.SlidingWindowLogRateLimiter;
import com.ratelimiter.algorithms.TokenBucketRateLimiter;

public class RateLimiterFactory {

    public enum Algorithm {
        TOKEN_BUCKET,
        FIXED_WINDOW,
        SLIDING_WINDOW_LOG,
        LEAKY_BUCKET
    }

    public static RateLimiter create(Algorithm algorithm, RateLimiterConfig config) {
        return switch (algorithm) {
            case TOKEN_BUCKET       -> new TokenBucketRateLimiter(config);
            case FIXED_WINDOW       -> new FixedWindowRateLimiter(config);
            case SLIDING_WINDOW_LOG -> new SlidingWindowLogRateLimiter(config);
            case LEAKY_BUCKET       -> new LeakyBucketRateLimiter(config);
        };
    }
}
