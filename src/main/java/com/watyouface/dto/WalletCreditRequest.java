package com.watyouface.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class WalletCreditRequest {
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double amount;

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
