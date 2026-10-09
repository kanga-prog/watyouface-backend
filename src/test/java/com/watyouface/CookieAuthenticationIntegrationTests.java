package com.watyouface;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.watyouface.entity.Contract;
import com.watyouface.entity.User;
import com.watyouface.entity.enums.Role;
import com.watyouface.repository.ContractRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.security.AuthCookieFactory;
import com.watyouface.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.servlet.http.Cookie;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=cookie-auth-test-secret-with-more-than-32-bytes",
        "app.cors.allowed-origins=http://localhost:5173",
        "app.security.jwt.allow-bearer-fallback=false",
        "app.security.auth-cookie.secure=false",
        "app.security.auth-cookie.same-site=Lax"
})
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class CookieAuthenticationIntegrationTests {
    private static final String TEST_PASSWORD = "CookieTestPassword!123";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository users;
    @Autowired private ContractRepository contracts;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    private User user;

    @BeforeEach
    void createAcceptedUser() {
        Contract active = contracts.findByActiveTrue().orElseGet(() -> {
            Contract contract = new Contract();
            contract.setActive(true);
            contract.setTitle("Test contract");
            contract.setVersion("test");
            contract.setContent("Test only");
            return contracts.saveAndFlush(contract);
        });
        String suffix = Long.toString(System.nanoTime());
        user = new User("cookie-user-" + suffix, "cookie-" + suffix + "@example.test", passwordEncoder.encode(TEST_PASSWORD));
        user.setRole(Role.USER);
        user.setAcceptedContract(true);
        user.setAcceptedContractVersion(active);
        user = users.saveAndFlush(user);
    }

    @Test
    void loginSetsHttpOnlyCookieAndDoesNotReturnJwtInJson() throws Exception {
        MvcResult result = loginWithCsrf();
        String setCookie = result.getResponse().getHeader("Set-Cookie");
        assertThat(setCookie).contains("WATYOUFACE_AUTH=", "HttpOnly", "Path=/", "SameSite=Lax", "Max-Age=86400");
        assertThat(setCookie).doesNotContain("Secure"); // local HTTP profile only
        assertThat(objectMapper.readTree(result.getResponse().getContentAsString()).has("token")).isFalse();
    }

    @Test
    void validAuthCookieAuthenticatesProtectedRoute() throws Exception {
        mvc.perform(get("/api/users/me").cookie(authCookie(validToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void protectedRouteWithoutCookieReturnsUnauthorized() throws Exception {
        mvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void invalidAuthCookieReturnsUnauthorized() throws Exception {
        mvc.perform(get("/api/users/me").cookie(authCookie("invalid.jwt.value")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void expiredAuthCookieReturnsUnauthorized() throws Exception {
        String expired = io.jsonwebtoken.Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("userId", user.getId())
                .setExpiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        "cookie-auth-test-secret-with-more-than-32-bytes".getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();
        mvc.perform(get("/api/users/me").cookie(authCookie(expired)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutExpiresTheAuthCookieUsingMatchingAttributes() throws Exception {
        MvcResult csrf = csrfToken();
        mvc.perform(post("/api/auth/logout")
                        .cookie(csrfCookie(csrf))
                        .header("X-XSRF-TOKEN", csrfValue(csrf)))
                .andExpect(status().isNoContent())
                .andExpect(result -> {
                    String value = result.getResponse().getHeader("Set-Cookie");
                    assertThat(value).contains("WATYOUFACE_AUTH=", "Max-Age=0", "Path=/", "HttpOnly", "SameSite=Lax");
                    assertThat(value).doesNotContain("Secure");
                });
    }

    @Test
    void corsPreflightAllowsCredentialsAndCsrfHeaderOnlyForConfiguredOrigin() throws Exception {
        mvc.perform(options("/api/auth/login")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type,x-xsrf-token"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    assertThat(result.getResponse().getHeader("Access-Control-Allow-Credentials")).isEqualTo("true");
                    assertThat(result.getResponse().getHeader("Access-Control-Allow-Origin")).isEqualTo("http://localhost:5173");
                    assertThat(result.getResponse().getHeader("Access-Control-Allow-Headers").toLowerCase()).contains("x-xsrf-token");
                });
    }

    @Test
    void csrfTokenIsRequiredForCookieLogin() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void csrfTokenAllowsCookieLogin() throws Exception {
        MvcResult result = loginWithCsrf();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getHeader("Set-Cookie")).contains("WATYOUFACE_AUTH=", "HttpOnly");
    }

    @Test
    void bearerHeaderIsRejectedWhenCookieMigrationModeIsStrict() throws Exception {
        mvc.perform(get("/api/users/me").header("Authorization", "Bearer " + validToken()))
                .andExpect(status().isUnauthorized());
    }

    private MvcResult loginWithCsrf() throws Exception {
        MvcResult csrf = csrfToken();
        return mvc.perform(post("/api/auth/login")
                        .cookie(csrfCookie(csrf))
                        .header("X-XSRF-TOKEN", csrfValue(csrf))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody()))
                .andExpect(status().isOk())
                .andReturn();
    }

    private MvcResult csrfToken() throws Exception {
        return mvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();
    }

    private String csrfValue(MvcResult result) throws Exception {
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.get("token").asText();
    }

    private Cookie csrfCookie(MvcResult result) {
        return cookieFromSetCookie(result.getResponse().getHeader("Set-Cookie"), "XSRF-TOKEN");
    }

    private Cookie authCookie(String token) {
        return new MockCookie(AuthCookieFactory.COOKIE_NAME, token);
    }

    private Cookie cookieFromSetCookie(String header, String name) {
        assertThat(header).startsWith(name + "=");
        String value = header.substring(name.length() + 1).split(";", 2)[0];
        return new MockCookie(name, value);
    }

    private String loginBody() throws Exception {
        return objectMapper.writeValueAsString(Map.of("email", user.getEmail(), "password", TEST_PASSWORD));
    }

    private String validToken() {
        return jwtUtil.generateToken(user.getId(), user.getUsername(), "USER");
    }
}
