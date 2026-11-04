package com.watyouface.service;

import com.watyouface.exception.StepUpDeliveryException;
import com.watyouface.security.StepUpAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "true")
public class SmtpStepUpCodeSender implements StepUpCodeSender {
    private static final Logger log = LoggerFactory.getLogger(SmtpStepUpCodeSender.class);
    private static final int MAX_SAFE_ROOT_MESSAGE_LENGTH = 240;
    private static final Pattern SENSITIVE_LOG_CONTENT = Pattern.compile(
            "(?i)(?:\\b[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}\\b|\\b\\d{6}\\b|"
                    + "\\b(?:mail_)?password\\b|\\b(?:mail_)?username\\b|"
                    + "\\b(?:jwt|token|cookie|secret|authorization)\\b)");

    private final MailService mailService;

    public SmtpStepUpCodeSender(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void send(String recipient, StepUpAction action, String code) {
        try {
            mailService.sendStepUpCode(recipient, action, code);
        } catch (Exception exception) {
            logFailure(action, exception);
            throw new StepUpDeliveryException();
        }
    }

    private void logFailure(StepUpAction action, Exception exception) {
        Throwable rootCause = rootCause(exception);
        String safeRootMessage = safeRootMessage(rootCause);

        if (safeRootMessage == null) {
            log.warn("SMTP step-up delivery failed: action={}, exceptionClass={}, rootCauseClass={}",
                    action, exception.getClass().getSimpleName(), rootCause.getClass().getSimpleName());
            return;
        }

        log.warn("SMTP step-up delivery failed: action={}, exceptionClass={}, rootCauseClass={}, rootCauseMessage={}",
                action, exception.getClass().getSimpleName(), rootCause.getClass().getSimpleName(), safeRootMessage);
    }

    private Throwable rootCause(Throwable exception) {
        Throwable current = exception;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private String safeRootMessage(Throwable rootCause) {
        String message = rootCause.getMessage();
        if (message == null) return null;

        String normalized = message.replaceAll("[\\r\\n\\t]+", " ").trim();
        if (normalized.isEmpty() || normalized.length() > MAX_SAFE_ROOT_MESSAGE_LENGTH
                || SENSITIVE_LOG_CONTENT.matcher(normalized).find()) {
            return null;
        }
        return normalized;
    }
}
