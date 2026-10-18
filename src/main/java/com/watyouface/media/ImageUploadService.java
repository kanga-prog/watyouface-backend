package com.watyouface.media;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Politique commune Feed/Marketplace. GIF et BMP sont normalisés en JPG : seule
 * la première frame d'un GIF est donc conservée, sans promesse d'animation.
 */
@Service
public class ImageUploadService {

    public static final long MAX_BYTES = 10L * 1024 * 1024;
    private static final int MAX_DIMENSION = 1400;
    private static final long MAX_PIXELS = 25_000_000L;
    private static final Map<String, String> MIME_TO_FORMAT = Map.of(
            "image/jpeg", "jpeg",
            "image/png", "png",
            "image/gif", "gif",
            "image/bmp", "bmp"
    );
    private final MediaStorageService storage;

    public ImageUploadService(MediaStorageService storage) {
        this.storage = storage;
    }

    public static boolean isSupportedMime(String mime) {
        return MIME_TO_FORMAT.containsKey(mime);
    }

    public static Set<String> supportedMimes() {
        return MIME_TO_FORMAT.keySet();
    }

    public String store(MultipartFile file, String directory, String prefix) throws IOException {
        validateEnvelope(file);
        BufferedImage image = decodeAndVerifyMime(file);
        validateDimensions(image);

        String filename = prefix + "_" + UUID.randomUUID() + ".jpg";
        String relative = directory + "/" + filename;
        Path output = Path.of(storage.resolvePath(relative));
        Path temporary = output.resolveSibling("." + filename + ".tmp");

        try {
            // Write explicitly as JPEG. The temporary filename deliberately
            // ends in .tmp, so relying on a filename-based writer selection
            // would make valid images fail at runtime.
            BufferedImage normalized = Thumbnails.of(image)
                    .size(MAX_DIMENSION, MAX_DIMENSION)
                    .outputQuality(0.88)
                    .asBufferedImage();
            if (!ImageIO.write(normalized, "jpg", temporary.toFile())) {
                throw new IOException("Encodeur JPEG indisponible");
            }
            Files.move(temporary, output, StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException e) {
            try {
                Files.move(temporary, output);
            } catch (IOException moveFailure) {
                cleanup(temporary, output);
                throw moveFailure;
            }
        } catch (IOException e) {
            cleanup(temporary, output);
            throw e;
        } catch (RuntimeException e) {
            cleanup(temporary, output);
            throw new IllegalArgumentException("Image invalide ou format non lisible");
        }
        return storage.publicUrl(relative);
    }

    private void validateEnvelope(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image manquante ou vide");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Image trop volumineuse : 10 Mo maximum.");
        }
        if (!isSupportedMime(file.getContentType())) {
            throw new IllegalArgumentException("Format d'image non autorisé");
        }
    }

    private BufferedImage decodeAndVerifyMime(MultipartFile file) {
        try (ImageInputStream stream = ImageIO.createImageInputStream(file.getInputStream())) {
            if (stream == null) throw new IllegalArgumentException("Image invalide ou format non lisible");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new IllegalArgumentException("Image invalide ou format non lisible");

            ImageReader reader = readers.next();
            try {
                reader.setInput(stream, true, true);
                String actualFormat = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!actualFormat.equals(MIME_TO_FORMAT.get(file.getContentType()))) {
                    throw new IllegalArgumentException("Le contenu ne correspond pas au type MIME déclaré");
                }
                BufferedImage image = reader.read(0);
                if (image == null) throw new IllegalArgumentException("Image invalide ou format non lisible");
                return image;
            } finally {
                reader.dispose();
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IOException | RuntimeException e) {
            throw new IllegalArgumentException("Image invalide ou format non lisible");
        }
    }

    private void validateDimensions(BufferedImage image) {
        if (image.getWidth() <= 0 || image.getHeight() <= 0
                || (long) image.getWidth() * image.getHeight() > MAX_PIXELS) {
            throw new IllegalArgumentException("Dimensions d'image invalides ou trop grandes");
        }
    }

    private void cleanup(Path temporary, Path output) {
        try { Files.deleteIfExists(temporary); } catch (IOException ignored) { }
        try { Files.deleteIfExists(output); } catch (IOException ignored) { }
    }
}
