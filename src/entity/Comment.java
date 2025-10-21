package com.watyouface.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private User author;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Video video;

    // Getters & Setters
}
