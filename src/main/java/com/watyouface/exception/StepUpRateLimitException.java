package com.watyouface.exception;

public class StepUpRateLimitException extends RuntimeException {
    private final long retryAfterSeconds;

    public StepUpRateLimitException(long retryAfterSeconds) {
        super("Trop de demandes de vérification. Réessayez plus tard.");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() { return retryAfterSeconds; }
}
