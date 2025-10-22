package com.watyouface.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @ManyToOne private User uploader;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<Like> likes;

    // Getters & Setters
}
