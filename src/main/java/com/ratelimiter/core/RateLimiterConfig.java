package com.ratelimiter.core;

import java.time.Duration;

public class RateLimiterConfig {

    private final int maxRequests;
    private final Duration windowDuration;

    private RateLimiterConfig(Builder builder) {
        this.maxRequests = builder.maxRequests;
        this.windowDuration = builder.windowDuration;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public Duration getWindowDuration() {
        return windowDuration;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int maxRequests;
        private Duration windowDuration;

        public Builder maxRequests(int maxRequests) {
            if (maxRequests <= 0) throw new IllegalArgumentException("maxRequests must be positive");
            this.maxRequests = maxRequests;
            return this;
        }

        public Builder windowDuration(Duration windowDuration) {
            if (windowDuration == null || windowDuration.isNegative() || windowDuration.isZero()) {
                throw new IllegalArgumentException("windowDuration must be positive");
            }
            this.windowDuration = windowDuration;
            return this;
        }

        public RateLimiterConfig build() {
            if (maxRequests == 0) throw new IllegalStateException("maxRequests is required");
            if (windowDuration == null) throw new IllegalStateException("windowDuration is required");
            return new RateLimiterConfig(this);
        }
    }
}
