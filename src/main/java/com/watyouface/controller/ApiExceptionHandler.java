package com.watyouface.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import com.watyouface.exception.ResourceNotFoundException;
import com.watyouface.exception.StepUpDeliveryException;
import com.watyouface.exception.StepUpRateLimitException;
import com.watyouface.exception.MediaUploadException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MediaUploadException.class)
    public ResponseEntity<?> mediaUpload(MediaUploadException e) {
        if (e.isServerError()) {
            log.error("Media upload processing failed", e);
        }
        return ResponseEntity.status(e.getStatus()).body(Map.of("error", e.responseMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> uploadTooLarge(MaxUploadSizeExceededException e) {
        log.warn("Multipart request rejected because it exceeds the configured size limit");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Map.of("error", "Fichier trop volumineux"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> notFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> forbidden(SecurityException e) {
        return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> conflict(IllegalStateException e) {
        return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(StepUpRateLimitException.class)
    public ResponseEntity<?> stepUpRateLimited(StepUpRateLimitException e) {
        return ResponseEntity.status(429)
                .header("Retry-After", Long.toString(e.getRetryAfterSeconds()))
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(StepUpDeliveryException.class)
    public ResponseEntity<?> stepUpDeliveryUnavailable(StepUpDeliveryException e) {
        return ResponseEntity.status(503).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Requête invalide");
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> accessDenied() {
        return ResponseEntity.status(403).body(Map.of("error", "Interdit"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> noResource() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Ressource introuvable"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> unexpected(Exception e) {
        log.error("Unhandled request failure", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Une erreur interne est survenue"));
    }
}
