package com.watyouface.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Per-instance fixed-window limiter for failed login attempts, keyed by remote client address. */
@Component
public class LoginRateLimiter {

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final long windowMillis;
    private final Clock clock;
    private final AtomicLong operations = new AtomicLong();

    @Autowired
    public LoginRateLimiter(
            @Value("${app.security.login-rate-limit.max-attempts:5}") int maxAttempts,
            @Value("${app.security.login-rate-limit.window-seconds:60}") long windowSeconds) {
        this(maxAttempts, windowSeconds, Clock.systemUTC());
    }

    public LoginRateLimiter(int maxAttempts, long windowSeconds, Clock clock) {
        if (maxAttempts < 1 || windowSeconds < 1) {
            throw new IllegalArgumentException("Rate limit settings must be positive");
        }
        this.maxAttempts = maxAttempts;
        this.windowMillis = windowSeconds * 1000;
        this.clock = clock;
    }

    /** Record one failed authentication. Credentials are never stored. */
    public void recordFailure(String key) {
        long now = clock.millis();
        windows.compute(key, (ignored, current) -> {
            if (current == null || expired(current, now)) {
                return new Window(now, 1);
            }
            return new Window(current.startedAtMillis(), current.failures() + 1);
        });
        cleanupOccasionally(now);
    }

    /** Returns a positive retry delay in seconds while the key is blocked, otherwise zero. */
    public long retryAfterSeconds(String key) {
        long now = clock.millis();
        Window window = windows.computeIfPresent(key,
                (ignored, current) -> expired(current, now) ? null : current);
        if (window == null || window.failures() < maxAttempts) {
            return 0;
        }
        long remaining = window.startedAtMillis() + windowMillis - now;
        return Math.max(1, (remaining + 999) / 1000);
    }

    public void clear(String key) {
        windows.remove(key);
    }

    private boolean expired(Window window, long now) {
        return now - window.startedAtMillis() >= windowMillis;
    }

    private void cleanupOccasionally(long now) {
        if (operations.incrementAndGet() % 64 == 0) {
            windows.entrySet().removeIf(entry -> expired(entry.getValue(), now));
        }
    }

    private record Window(long startedAtMillis, int failures) {}
}
