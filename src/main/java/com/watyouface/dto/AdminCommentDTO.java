package com.watyouface.dto;

import java.time.LocalDateTime;

public record AdminCommentDTO(
        Long id,
        Long authorId,
        String authorUsername,
        Long postId,
        String postExcerpt,
        String content,
        LocalDateTime createdAt
) {}
