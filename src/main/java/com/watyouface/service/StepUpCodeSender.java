package com.watyouface.service;

import com.watyouface.security.StepUpAction;

public interface StepUpCodeSender {
    void send(String recipient, StepUpAction action, String code);
}
