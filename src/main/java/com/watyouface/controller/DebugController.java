package com.watyouface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Profile;

import java.io.File;

@RestController
@Profile("dev")
public class DebugController {

    @GetMapping("/test-upload")
    public String testUpload() {
        String path = System.getProperty("user.dir") + "/media";
        File dir = new File(path);
        return "Media storage available: " + dir.isDirectory();
    }
}
