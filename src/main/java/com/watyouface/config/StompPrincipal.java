// src/main/java/com/watyouface/config/StompPrincipal.java
package com.watyouface.config;

import java.security.Principal;

public class StompPrincipal implements Principal {
    private final String name;
    private final Long userId;

    public StompPrincipal(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }

    @Override
    public String getName() { return name; }

    public Long getUserId() { return userId; }
}
