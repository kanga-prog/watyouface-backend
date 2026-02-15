package com.watyouface.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Authz {

    public Long me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new SecurityException("Non authentifié");
        }
        return (Long) auth.getPrincipal();
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public void ownerOrAdmin(Long ownerId) {
        Long me = me();
        if (!isAdmin() && !me.equals(ownerId)) {
            throw new SecurityException("Interdit");
        }
    }
}
