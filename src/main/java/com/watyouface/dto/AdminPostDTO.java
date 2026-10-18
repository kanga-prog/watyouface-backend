package com.watyouface.dto;

import java.time.LocalDateTime;

public record AdminPostDTO(
        Long id,
        Long authorId,
        String authorUsername,
        LocalDateTime createdAt,
        String content,
        String imageUrl,
        String videoUrl
) {}
