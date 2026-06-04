# Rate Limiter

A Java implementation of four classic rate limiting algorithms behind a common interface.

## Algorithms

| Algorithm | Burst Support | Memory | Output |
|---|---|---|---|
| **Token Bucket** | Yes (up to capacity) | O(1) | Smooth avg, allows bursts |
| **Fixed Window** | Yes (at boundaries) | O(1) | Simple, boundary spike risk |
| **Sliding Window Log** | No | O(limit) | Precise, no boundary spikes |
| **Leaky Bucket** | No | O(1) | Perfectly smooth |

## Project Structure

```
src/
├── main/java/com/ratelimiter/
│   ├── core/
│   │   ├── RateLimiter.java           # Interface
│   │   ├── RateLimiterConfig.java     # Builder-style config
│   │   └── RateLimiterFactory.java    # Factory by algorithm
│   ├── algorithms/
│   │   ├── TokenBucketRateLimiter.java
│   │   ├── FixedWindowRateLimiter.java
│   │   ├── SlidingWindowLogRateLimiter.java
│   │   └── LeakyBucketRateLimiter.java
│   └── demo/
│       └── RateLimiterDemo.java
└── test/java/com/ratelimiter/
    └── RateLimiterTest.java
```

## Usage

```java
RateLimiterConfig config = RateLimiterConfig.builder()
    .maxRequests(100)
    .windowDuration(Duration.ofSeconds(1))
    .build();

RateLimiter limiter = RateLimiterFactory.create(Algorithm.TOKEN_BUCKET, config);

if (limiter.tryAcquire()) {
    // handle request
} else {
    // return 429 Too Many Requests
}
```

## Build & Run

**Requirements:** Java 17+, Maven 3.9+

```bash
# Run tests
mvn test

# Run demo
mvn package -q && java -jar target/rate-limiter-1.0.0.jar
```

## Docker

```bash
# Build
docker build -t rate-limiter .

# Run demo
docker run --rm rate-limiter
```

## Demo Output

```
=== Rate Limiter Demo ===
Config: 5 requests per 1000ms

--- TokenBucket[capacity=5, refillRate=5.00 tokens/s] ---
  Request 1: ALLOWED
  ...
  Request 6: DENIED
  Result: 5 allowed, 3 denied
```
