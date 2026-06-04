package com.ratelimiter.algorithms;

import com.ratelimiter.core.RateLimiter;
import com.ratelimiter.core.RateLimiterConfig;

/**
 * Leaky Bucket algorithm.
 *
 * Requests enter a fixed-capacity queue and are processed ("leaked") at a
 * constant rate. Incoming requests are rejected when the queue is full.
 *
 * Enforces a perfectly smooth output rate — no bursts ever pass through —
 * making it ideal for downstream systems that cannot absorb spikes.
 */
public class LeakyBucketRateLimiter implements RateLimiter {

    private final int capacity;
    private final double leakRatePerNano;

    private double queueSize;
    private long lastLeakNanos;

    public LeakyBucketRateLimiter(RateLimiterConfig config) {
        this.capacity = config.getMaxRequests();
        this.leakRatePerNano = (double) capacity / config.getWindowDuration().toNanos();
        this.queueSize = 0;
        this.lastLeakNanos = System.nanoTime();
    }

    @Override
    public synchronized boolean tryAcquire() {
        leak();
        // Use queueSize + 1 <= capacity so that a fractional leak cannot open
        // a slot that was already consumed (avoids floating-point overshoot).
        if (queueSize + 1.0 <= capacity) {
            queueSize++;
            return true;
        }
        return false;
    }

    private void leak() {
        long now = System.nanoTime();
        long elapsed = now - lastLeakNanos;
        double leaked = elapsed * leakRatePerNano;
        queueSize = Math.max(0, queueSize - leaked);
        lastLeakNanos = now;
    }

    @Override
    public String describe() {
        return String.format("LeakyBucket[capacity=%d, leakRate=%.2f requests/s]",
                capacity, leakRatePerNano * 1_000_000_000);
    }
}
