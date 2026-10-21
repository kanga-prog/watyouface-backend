package com.watyouface.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Opt-in local administration command. Disabled by default in every profile. */
@Component
public class MediaReconciliationCommand implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(MediaReconciliationCommand.class);
    private static final String EXECUTE_CONFIRMATION = "PURGE_ORPHANS";

    private final MediaReconciliationService reconciliation;
    private final boolean enabled;
    private final String mode;
    private final String confirmation;

    public MediaReconciliationCommand(MediaReconciliationService reconciliation,
                                      @Value("${app.media.reconciliation.enabled:false}") boolean enabled,
                                      @Value("${app.media.reconciliation.mode:dry-run}") String mode,
                                      @Value("${app.media.reconciliation.confirmation:}") String confirmation) {
        this.reconciliation = reconciliation;
        this.enabled = enabled;
        this.mode = mode;
        this.confirmation = confirmation;
    }

    @Override
    public void run(ApplicationArguments arguments) {
        if (!enabled) return;
        boolean execute = "execute".equalsIgnoreCase(mode);
        if (execute && !EXECUTE_CONFIRMATION.equals(confirmation)) {
            throw new IllegalStateException("Purge refusée : confirmation explicite manquante");
        }
        MediaReconciliationReport report = execute ? reconciliation.executePurge() : reconciliation.dryRun();
        log.info("Media reconciliation {}: scanned={}, referenced={}, orphan={}, unknown={}, protected={}, recoverableBytes={}, deleted={}, invalidDbReferences={}",
                execute ? "execute" : "dry-run", report.filesScanned(), report.referencedFiles(), report.orphanFiles(),
                report.unknownFiles(), report.protectedFiles(), report.recoverableBytes(), report.filesDeleted(),
                report.invalidDatabaseReferences());
    }
}
