package com.watyouface.dto;

import com.watyouface.security.StepUpAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StepUpRequest {
    @NotNull
    private StepUpAction action;

    @NotBlank
    @Size(max = 128)
    private String currentPassword;

    public StepUpAction getAction() { return action; }
    public void setAction(StepUpAction action) { this.action = action; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
}
