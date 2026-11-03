package com.watyouface.exception;

import org.springframework.http.HttpStatus;

/**
 * Carries a safe client status for expected media-upload outcomes.  It extends
 * IllegalArgumentException for compatibility with legacy service call sites.
 */
public class MediaUploadException extends IllegalArgumentException {

    private final HttpStatus status;

    private MediaUploadException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    private MediaUploadException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public boolean isServerError() {
        return status.is5xxServerError();
    }

    public String responseMessage() {
        return isServerError() ? "Une erreur interne est survenue" : getMessage();
    }

    public static MediaUploadException badRequest(String message) {
        return new MediaUploadException(HttpStatus.BAD_REQUEST, message);
    }

    public static MediaUploadException payloadTooLarge(String message) {
        return new MediaUploadException(HttpStatus.PAYLOAD_TOO_LARGE, message);
    }

    public static MediaUploadException unsupportedMediaType(String message) {
        return new MediaUploadException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message);
    }

    public static MediaUploadException unprocessable(String message) {
        return new MediaUploadException(HttpStatus.UNPROCESSABLE_ENTITY, message);
    }

    public static MediaUploadException storageFailure(Throwable cause) {
        return new MediaUploadException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Le traitement du média a échoué", cause);
    }
}
