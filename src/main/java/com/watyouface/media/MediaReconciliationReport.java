package com.watyouface.media;

import java.util.List;

public record MediaReconciliationReport(
        boolean dryRun,
        int filesScanned,
        int referencedFiles,
        int orphanFiles,
        int unknownFiles,
        int protectedFiles,
        int invalidDatabaseReferences,
        long totalBytes,
        long recoverableBytes,
        int filesDeleted,
        List<String> anomalies,
        List<MediaInventoryEntry> entries
) { }
