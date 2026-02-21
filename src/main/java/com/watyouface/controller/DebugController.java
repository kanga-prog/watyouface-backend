package com.watyouface.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
public class DebugController {

    @GetMapping("/test-upload")
    public String testUpload() {
        String path = System.getProperty("user.dir") + "/media";
        File dir = new File(path);
        return "Uploads exists: " + dir.exists() + ", files: " + java.util.Arrays.toString(dir.list());
    }
}