package com.watyouface.config;

import com.watyouface.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.*;

import java.util.List;
import org.springframework.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;
  private final String[] allowedOrigins;
  private final boolean hstsEnabled;
  private final boolean secureCookies;
  private final String cookieSameSite;
  private final boolean allowBearerFallback;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                        @Value("${app.cors.allowed-origins:}") String allowedOrigins,
                        @Value("${app.security.hsts.enabled:false}") boolean hstsEnabled,
                        @Value("${app.security.auth-cookie.secure:false}") boolean secureCookies,
                        @Value("${app.security.auth-cookie.same-site:Lax}") String cookieSameSite,
                        @Value("${app.security.jwt.allow-bearer-fallback:false}") boolean allowBearerFallback) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.allowedOrigins = StringUtils.commaDelimitedListToStringArray(allowedOrigins.trim());
    if (List.of(this.allowedOrigins).stream().anyMatch(origin -> origin.equals("*"))) {
      throw new IllegalArgumentException("CORS wildcard origin is not allowed");
    }
    this.hstsEnabled = hstsEnabled;
    this.secureCookies = secureCookies;
    this.cookieSameSite = cookieSameSite;
    this.allowBearerFallback = allowBearerFallback;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf
        .csrfTokenRepository(csrfTokenRepository())
        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        // WebSocket/SockJS is separately protected by an explicit Origin allowlist,
        // the authenticated cookie handshake, and per-conversation STOMP checks.
        .ignoringRequestMatchers("/ws/**")
        // Bearer compatibility is enabled only in local/dev profiles; explicit headers
        // are not ambient browser credentials. Production disables this fallback.
        .ignoringRequestMatchers(legacyBearerRequestMatcher()))
      .cors(cors -> cors.configurationSource(corsConfigurationSource()))
      .headers(headers -> {
        headers.contentTypeOptions(Customizer.withDefaults())
          .frameOptions(frame -> frame.deny())
          .referrerPolicy(referrer -> referrer.policy(
              ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN));
        if (hstsEnabled) {
          headers.httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000));
        } else {
          headers.httpStrictTransportSecurity(hsts -> hsts.disable());
        }
      })
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(exceptions -> exceptions
        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/marketplace/listings/upload").authenticated()

        // ✅ PUBLIC MEDIA
        .requestMatchers("/media/**").permitAll()

        // ✅ PUBLIC AUTH + WS
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers("/api/contracts/active").permitAll()
        .requestMatchers("/ws/**", "/ws/info/**").permitAll()

        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  private RequestMatcher legacyBearerRequestMatcher() {
    return request -> allowBearerFallback
        && request.getHeader("Authorization") != null
        && request.getHeader("Authorization").startsWith("Bearer ");
  }

  @Bean
  public CsrfTokenRepository csrfTokenRepository() {
    CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    repository.setCookieName("XSRF-TOKEN");
    repository.setHeaderName("X-XSRF-TOKEN");
    repository.setCookiePath("/");
    repository.setCookieCustomizer(cookie -> cookie
        .sameSite(cookieSameSite)
        .secure(secureCookies));
    return repository;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(allowedOrigins));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "X-XSRF-TOKEN"));
    config.setExposedHeaders(List.of());
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
}
