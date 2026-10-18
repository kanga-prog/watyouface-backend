package com.watyouface.dto;

import java.time.LocalDateTime;

public record AdminListingDTO(
        Long id,
        String title,
        String description,
        Long sellerId,
        String sellerUsername,
        Double price,
        String status,
        String image,
        LocalDateTime createdAt,
        boolean hidden
) {}
