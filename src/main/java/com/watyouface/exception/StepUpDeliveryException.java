package com.watyouface.exception;

public class StepUpDeliveryException extends RuntimeException {
    public StepUpDeliveryException() {
        super("Le code de vérification n'a pas pu être envoyé. Réessayez plus tard.");
    }
}
