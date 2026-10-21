package com.watyouface.dto;

import java.time.Instant;

public record StepUpTokenResponse(String stepUpToken, Instant expiresAt) { }
