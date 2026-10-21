package com.watyouface.dto;

import com.watyouface.security.StepUpAction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

public class StepUpVerifyRequest {
    @NotNull
    private StepUpAction action;

    @NotBlank
    @Pattern(regexp = "\\d{6}", message = "Le code doit contenir 6 chiffres")
    private String code;

    public StepUpAction getAction() { return action; }
    public void setAction(StepUpAction action) { this.action = action; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
