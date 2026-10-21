package com.watyouface.service;

import com.watyouface.exception.StepUpDeliveryException;
import com.watyouface.security.StepUpAction;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "true")
public class SmtpStepUpCodeSender implements StepUpCodeSender {
    private final MailService mailService;

    public SmtpStepUpCodeSender(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void send(String recipient, StepUpAction action, String code) {
        try {
            mailService.sendStepUpCode(recipient, action, code);
        } catch (Exception exception) {
            throw new StepUpDeliveryException();
        }
    }
}
