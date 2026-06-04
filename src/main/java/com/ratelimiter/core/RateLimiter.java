package com.ratelimiter.core;

/**
 * Contract for all rate limiter implementations.
 *
 * Each implementation controls how many requests a caller can make within
 * a given time window, differing in how they track and enforce that limit.
 */
public interface RateLimiter {

    /**
     * Attempts to acquire permission to proceed with a request.
     *
     * @return true if the request is allowed, false if it should be rejected
     */
    boolean tryAcquire();

    /**
     * Returns a human-readable description of this limiter's configuration.
     */
    String describe();
}
