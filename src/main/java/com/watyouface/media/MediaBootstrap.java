package com.watyouface.media;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;

@Component
public class MediaBootstrap {

    private final MediaStorageService storage;

    public MediaBootstrap(MediaStorageService storage) {
        this.storage = storage;
    }

    @PostConstruct
    public void init() {
        // crée les dossiers attendus
        storage.resolvePath("avatars/.keep");
        storage.resolvePath("posts/.keep");
        storage.resolvePath("videos/.keep");

        // assure un default avatar minimal (1x1 png transparent)
        String defaultPath = storage.resolvePath("avatars/default.png");
        File f = new File(defaultPath);
        if (!f.exists()) {
            try (FileOutputStream out = new FileOutputStream(f)) {
                out.write(DEFAULT_PNG_1X1);
            } catch (Exception ignored) {
            }
        }
    }

    // PNG 1x1 transparent
    private static final byte[] DEFAULT_PNG_1X1 = new byte[] {
            (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte)0xC4, (byte)0x89,
            0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54,
            0x78, (byte)0x9C, 0x63, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x01,
            0x0D, 0x0A, 0x2D, (byte)0xB4,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
            (byte)0xAE, 0x42, 0x60, (byte)0x82
    };
}
