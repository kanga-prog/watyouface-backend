package com.watyouface.dto;

import jakarta.validation.constraints.NotNull;

public class ContractAcceptanceRequest {
    @NotNull(message = "L'identifiant du contrat est obligatoire")
    private Long contractId;
    private boolean accepted;

    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
}
