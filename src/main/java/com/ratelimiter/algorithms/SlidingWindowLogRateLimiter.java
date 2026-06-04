package com.ratelimiter.algorithms;

import com.ratelimiter.core.RateLimiter;
import com.ratelimiter.core.RateLimiterConfig;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Sliding Window Log algorithm.
 *
 * Keeps a log of timestamps for every allowed request. On each call, entries
 * older than the window are evicted, then the request is allowed only if the
 * remaining log size is below the limit.
 *
 * Provides smooth, precise enforcement with no boundary burst problem, at the
 * cost of O(N) memory where N is the request limit per window.
 */
public class SlidingWindowLogRateLimiter implements RateLimiter {

    private final int maxRequests;
    private final long windowNanos;

    private final Deque<Long> timestamps = new ArrayDeque<>();

    public SlidingWindowLogRateLimiter(RateLimiterConfig config) {
        this.maxRequests = config.getMaxRequests();
        this.windowNanos = config.getWindowDuration().toNanos();
    }

    @Override
    public synchronized boolean tryAcquire() {
        long now = System.nanoTime();
        evictExpired(now);
        if (timestamps.size() < maxRequests) {
            timestamps.addLast(now);
            return true;
        }
        return false;
    }

    private void evictExpired(long now) {
        long cutoff = now - windowNanos;
        while (!timestamps.isEmpty() && timestamps.peekFirst() <= cutoff) {
            timestamps.pollFirst();
        }
    }

    @Override
    public String describe() {
        return String.format("SlidingWindowLog[maxRequests=%d, windowMs=%d]",
                maxRequests, windowNanos / 1_000_000);
    }
}
