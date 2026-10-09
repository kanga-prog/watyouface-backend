package com.watyouface.config;

import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Development must not require a reachable SMTP server. This sender accepts
 * messages locally without delivering them; production continues to rely on
 * Spring Mail configuration supplied by its environment.
 */
@Configuration
@Profile("dev")
public class DevMailConfig {

    @Bean
    JavaMailSender devMailSender() {
        return new JavaMailSenderImpl() {
            @Override
            public void send(MimeMessage... mimeMessages) {
                // Deliberately no-op: no recipient, subject or attachment is logged.
            }
        };
    }
}
