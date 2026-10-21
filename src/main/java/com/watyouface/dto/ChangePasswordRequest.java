package com.watyouface.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordRequest {
    @NotBlank
    @Size(max = 128)
    private String currentPassword;

    @NotBlank
    @Size(min = 12, max = 128, message = "Le nouveau mot de passe doit contenir entre 12 et 128 caractères")
    private String newPassword;

    @NotBlank
    @Size(max = 128)
    private String confirmPassword;

    @Size(max = 128)
    private String stepUpToken;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getStepUpToken() { return stepUpToken; }
    public void setStepUpToken(String stepUpToken) { this.stepUpToken = stepUpToken; }
}
