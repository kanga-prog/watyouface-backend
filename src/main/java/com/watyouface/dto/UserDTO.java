package com.watyouface.dto;

import com.watyouface.entity.User;

public class UserDTO {
    private Long id;
    private String username;
    private String avatarUrl;

    public UserDTO(User u) {
        this.id = u.getId();
        this.username = u.getUsername();
        this.avatarUrl = u.getAvatarUrl();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getAvatarUrl() { return avatarUrl; }
}
