package com.watyouface.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private LocalDateTime sentAt = LocalDateTime.now();

    @ManyToOne private User sender;
    @ManyToOne private User receiver;

    // Getters & Setters
}
