package com.watyouface;

import com.watyouface.config.CookieJwtHandshakeInterceptor;
import com.watyouface.config.StompPrincipal;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.Role;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.AuthCookieFactory;
import com.watyouface.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

import java.util.HashMap;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CookieJwtHandshakeTests {
    private static final String SECRET = "cookie-handshake-test-secret-with-at-least-32-bytes";

    @Test
    void validHttpOnlyAuthCookieEstablishesStompPrincipalFromDatabaseIdentity() throws Exception {
        JwtUtil jwtUtil = new JwtUtil(SECRET);
        UserRepository users = mock(UserRepository.class);
        User user = new User("chat-member", "chat-member@example.test", "encoded-password");
        user.setId(73L);
        user.setRole(Role.USER);
        user.setAvatarUrl("/media/avatar.png");
        String jwt = jwtUtil.generateToken(73L, user.getUsername(), "USER");
        when(users.findById(73L)).thenReturn(Optional.of(user));

        CookieJwtHandshakeInterceptor interceptor = new CookieJwtHandshakeInterceptor(jwtUtil, users);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, "other=value; " + AuthCookieFactory.COOKIE_NAME + "=" + jwt);
        when(request.getHeaders()).thenReturn(headers);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        var attributes = new HashMap<String, Object>();

        boolean allowed = interceptor.beforeHandshake(request, response, mock(WebSocketHandler.class), attributes);

        assertThat(allowed).isTrue();
        assertThat(attributes.get(CookieJwtHandshakeInterceptor.AUTHENTICATED_PRINCIPAL))
                .isInstanceOfSatisfying(StompPrincipal.class, principal -> {
                    assertThat(principal.getUserId()).isEqualTo(73L);
                    assertThat(principal.getName()).isEqualTo("chat-member");
                });
    }
}
