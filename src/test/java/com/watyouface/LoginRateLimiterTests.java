package com.watyouface;

import com.watyouface.security.LoginRateLimiter;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoginRateLimiterTests {

    @Test
    void attemptsUnderThresholdRemainAllowed() {
        MutableClock clock = new MutableClock();
        LoginRateLimiter limiter = new LoginRateLimiter(5, 60, clock);

        for (int i = 0; i < 4; i++) limiter.recordFailure("client-a");

        assertEquals(0, limiter.retryAfterSeconds("client-a"));
    }

    @Test
    void expiredWindowAllowsAttemptsAgainWithoutWaiting() {
        MutableClock clock = new MutableClock();
        LoginRateLimiter limiter = new LoginRateLimiter(5, 60, clock);
        for (int i = 0; i < 5; i++) limiter.recordFailure("client-a");
        assertEquals(60, limiter.retryAfterSeconds("client-a"));

        clock.advance(Duration.ofSeconds(60));

        assertEquals(0, limiter.retryAfterSeconds("client-a"));
        limiter.recordFailure("client-a");
        assertEquals(0, limiter.retryAfterSeconds("client-a"));
    }

    @Test
    void failuresForAnotherClientDoNotBlockThisClient() {
        LoginRateLimiter limiter = new LoginRateLimiter(5, 60, new MutableClock());
        for (int i = 0; i < 5; i++) limiter.recordFailure("client-a");

        assertEquals(0, limiter.retryAfterSeconds("client-b"));
        assertEquals(60, limiter.retryAfterSeconds("client-a"));
    }

    private static final class MutableClock extends Clock {
        private Instant now = Instant.parse("2026-10-08T10:00:00Z");

        void advance(Duration duration) { now = now.plus(duration); }

        @Override public ZoneId getZone() { return ZoneId.of("UTC"); }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }
}
