package com.ratelimiter.algorithms;

import com.ratelimiter.core.RateLimiter;
import com.ratelimiter.core.RateLimiterConfig;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Token Bucket algorithm.
 *
 * A bucket holds up to `capacity` tokens. Tokens are added at a fixed rate
 * (capacity / window). Each request consumes one token. Requests are denied
 * when the bucket is empty. Unused tokens accumulate up to the bucket capacity,
 * allowing short bursts above the average rate.
 */
public class TokenBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final double refillRatePerNano;

    private final AtomicLong tokens;
    private volatile long lastRefillNanos;

    public TokenBucketRateLimiter(RateLimiterConfig config) {
        this.capacity = config.getMaxRequests();
        this.refillRatePerNano = (double) capacity / config.getWindowDuration().toNanos();
        this.tokens = new AtomicLong(Double.doubleToRawLongBits(capacity));
        this.lastRefillNanos = System.nanoTime();
    }

    @Override
    public synchronized boolean tryAcquire() {
        refill();
        double current = Double.longBitsToDouble(tokens.get());
        if (current >= 1.0) {
            tokens.set(Double.doubleToRawLongBits(current - 1.0));
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillNanos;
        double newTokens = elapsed * refillRatePerNano;
        if (newTokens > 0) {
            double current = Double.longBitsToDouble(tokens.get());
            double updated = Math.min(capacity, current + newTokens);
            tokens.set(Double.doubleToRawLongBits(updated));
            lastRefillNanos = now;
        }
    }

    @Override
    public String describe() {
        return String.format("TokenBucket[capacity=%d, refillRate=%.2f tokens/s]",
                capacity, refillRatePerNano * 1_000_000_000);
    }
}
