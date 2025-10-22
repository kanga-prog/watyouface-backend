package com.watyouface.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();

    // L'auteur du commentaire
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // Le post commenté (optionnel)
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // La vidéo commentée (optionnel)
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    // --- Getters & Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }
}
