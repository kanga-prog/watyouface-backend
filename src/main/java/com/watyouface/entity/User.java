package com.watyouface.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.watyouface.entity.enums.Role;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users") // pour éviter le mot réservé "user" dans certaines BDD
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    
    @JsonIgnore
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean acceptedContract = false;

    @Column
    private String avatarUrl; // Ajout du champ avatarUrl

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    // Relations
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Post> posts;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Message> sentMessages;

    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Like> likes;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "contract_id")
    private Contract acceptedContractVersion;

    // --- Constructeurs ---
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public List<Message> getSentMessages() { return sentMessages; }
    public void setSentMessages(List<Message> sentMessages) { this.sentMessages = sentMessages; }

    public List<Message> getReceivedMessages() { return receivedMessages; }
    public void setReceivedMessages(List<Message> receivedMessages) { this.receivedMessages = receivedMessages; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<Like> getLikes() { return likes; }
    public void setLikes(List<Like> likes) { this.likes = likes; }

    public boolean isAcceptedContract() { return acceptedContract; }
    public void setAcceptedContract(boolean acceptedContract) { this.acceptedContract = acceptedContract; }

    public Contract getAcceptedContractVersion() { return acceptedContractVersion; }
    public void setAcceptedContractVersion(Contract acceptedContractVersion) { this.acceptedContractVersion = acceptedContractVersion; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}

