package com.watyouface.entity;

import jakarta.persistence.*;

@Entity
@Table(name="video_share")
public class VideoShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Video video;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    private boolean seen = false; // Optionnel : pour savoir si l'utilisateur a regardé la vidéo

    public VideoShare() {}

    public VideoShare(Video video, User sender, User receiver) {
        this.video = video;
        this.sender = sender;
        this.receiver = receiver;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public Video getVideo() { return video; }
    public User getSender() { return sender; }
    public User getReceiver() { return receiver; }
    public boolean isSeen() { return seen; }
}
