package com.watyouface.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ On peut garder l'utilisateur si besoin, mais souvent on ne renvoie que le compteur
    // Pour l'instant, on le masque pour simplifier
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // ✅ On garde le post (ou au moins son ID) si on veut savoir ce qui est liké
    // Mais pour éviter la boucle, on pourrait aussi le masquer et gérer via DTO
    // Ici, on le garde SANS @JsonIgnore → risque de boucle si Post.likes n'est pas masqué
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "video_id")
    @JsonIgnore
    private Video video;

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }
}