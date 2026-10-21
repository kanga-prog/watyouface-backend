package com.watyouface.media;

import java.time.Instant;

public record MediaInventoryEntry(
        String relativePath,
        String extension,
        long sizeBytes,
        Instant lastModified,
        MediaInventoryStatus status
) { }
