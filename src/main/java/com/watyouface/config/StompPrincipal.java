// src/main/java/com/watyouface/config/StompPrincipal.java
package com.watyouface.config;

import java.security.Principal;

public class StompPrincipal implements Principal {
    private final String name;
    private final Long userId;
    private final String avatarUrl;

    public StompPrincipal(String name, Long userId,String avatarUrl) {
        this.name = name;
        this.userId = userId;
        this.avatarUrl = avatarUrl;
    }

    @Override
    public String getName() { return name; }

    public Long getUserId() { return userId; }

    public String getAvatarUrl() { return avatarUrl; }
}
