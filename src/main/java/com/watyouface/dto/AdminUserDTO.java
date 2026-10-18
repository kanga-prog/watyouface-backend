package com.watyouface.dto;

public record AdminUserDTO(
        Long id,
        String username,
        String email,
        String role,
        boolean acceptedContract,
        Double walletBalance
) {}
