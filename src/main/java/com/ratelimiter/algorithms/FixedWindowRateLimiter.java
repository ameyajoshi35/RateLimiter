package com.ratelimiter.algorithms;

import com.ratelimiter.core.RateLimiter;
import com.ratelimiter.core.RateLimiterConfig;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed Window Counter algorithm.
 *
 * Time is divided into fixed-size windows. Each window tracks an independent
 * counter. The counter resets at the start of every new window.
 *
 * Simple and memory-efficient, but susceptible to burst traffic at window
 * boundaries: up to 2x the limit can pass in a short span straddling two windows.
 */
public class FixedWindowRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowNanos;

    private final AtomicInteger counter = new AtomicInteger(0);
    private volatile long windowStartNanos;

    public FixedWindowRateLimiter(RateLimiterConfig config) {
        this.maxRequests = config.getMaxRequests();
        this.windowNanos = config.getWindowDuration().toNanos();
        this.windowStartNanos = System.nanoTime();
    }

    @Override
    public synchronized boolean tryAcquire() {
        long now = System.nanoTime();
        if (now - windowStartNanos >= windowNanos) {
            counter.set(0);
            windowStartNanos = now;
        }
        if (counter.get() < maxRequests) {
            counter.incrementAndGet();
            return true;
        }
        return false;
    }

    @Override
    public String describe() {
        return String.format("FixedWindow[maxRequests=%d, windowMs=%d]",
                maxRequests, windowNanos / 1_000_000);
    }
}
