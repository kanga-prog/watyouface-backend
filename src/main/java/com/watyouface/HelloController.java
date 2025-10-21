package com.watyouface;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from profile: " + activeProfile;
    }
}
