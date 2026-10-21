package com.watyouface.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangeEmailRequest {
    @NotBlank
    @Email
    @Size(max = 255)
    private String newEmail;

    @NotBlank
    @Size(max = 128)
    private String currentPassword;

    @Size(max = 128)
    private String stepUpToken;

    public String getNewEmail() { return newEmail; }
    public void setNewEmail(String newEmail) { this.newEmail = newEmail == null ? null : newEmail.trim(); }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getStepUpToken() { return stepUpToken; }
    public void setStepUpToken(String stepUpToken) { this.stepUpToken = stepUpToken; }
}
