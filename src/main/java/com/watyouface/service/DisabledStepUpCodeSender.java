package com.watyouface.service;

import com.watyouface.security.StepUpAction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledStepUpCodeSender implements StepUpCodeSender {
    @Override
    public void send(String recipient, StepUpAction action, String code) {
        throw new IllegalStateException("L'envoi du code step-up n'est pas configuré.");
    }
}
