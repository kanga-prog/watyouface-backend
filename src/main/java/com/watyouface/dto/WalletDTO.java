package com.watyouface.dto;

public class WalletDTO {
    public Long userId;
    public Double balance;

    public WalletDTO() {}

    public WalletDTO(Long userId, Double balance) {
        this.userId = userId;
        this.balance = balance;
    }
}
