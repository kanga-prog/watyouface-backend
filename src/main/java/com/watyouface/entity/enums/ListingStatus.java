package com.watyouface.entity.enums;

public enum ListingStatus {
    AVAILABLE,   // visible par tous
    PENDING,     // discussion en cours
    ACCEPTED,    // vendeur a accepté
    PAID,        // acheteur a payé
    SHIPPED,     // vendeur a expédié
    RECEIVED,    // acheteur a confirmé réception
    REFUSED;     // vendeur a refusé

    public boolean canBePaid() {
        return this == ACCEPTED;
    }

    public boolean canBeShipped() {
        return this == PAID;
    }

    public boolean canBeReceived() {
        return this == SHIPPED;
    }

    public boolean isFinal() {
        return this == RECEIVED || this == REFUSED;
    }
}
