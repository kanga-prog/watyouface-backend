package com.watyouface.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne private User user;
    @ManyToOne private Post post;
    @ManyToOne private Video video;

    // Getters & Setters
}
