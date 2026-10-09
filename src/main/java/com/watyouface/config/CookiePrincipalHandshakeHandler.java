package com.watyouface.config;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.http.server.ServerHttpRequest;

import java.security.Principal;
import java.util.Map;

public class CookiePrincipalHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        Object principal = attributes.get(CookieJwtHandshakeInterceptor.AUTHENTICATED_PRINCIPAL);
        return principal instanceof Principal ? (Principal) principal : null;
    }
}
