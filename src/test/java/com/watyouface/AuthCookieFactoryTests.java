package com.watyouface;

import com.watyouface.security.AuthCookieFactory;
import com.watyouface.security.JwtUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthCookieFactoryTests {
    private static final String SECRET = "cookie-factory-test-secret-with-at-least-32-bytes";

    @Test
    void localHttpCookieIsHttpOnlyLaxAndAlignedToJwtExpiry() {
        AuthCookieFactory factory = new AuthCookieFactory(false, "Lax", new JwtUtil(SECRET));
        String cookie = factory.create("opaque-token").toString();
        assertThat(cookie).contains("HttpOnly", "Path=/", "SameSite=Lax", "Max-Age=86400");
        assertThat(cookie).doesNotContain("Secure");
    }

    @Test
    void productionHttpsCookieIsSecureAndSameSiteNoneCannotBeConfiguredWithoutSecure() {
        AuthCookieFactory factory = new AuthCookieFactory(true, "None", new JwtUtil(SECRET));
        assertThat(factory.create("opaque-token").toString()).contains("Secure", "HttpOnly", "SameSite=None");

        assertThatThrownBy(() -> new AuthCookieFactory(false, "None", new JwtUtil(SECRET)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("requires Secure");
    }
}
