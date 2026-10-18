package com.watyouface;

import com.watyouface.media.ImageService;
import com.watyouface.media.MediaStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FeedImageUploadTests {

    @TempDir Path tempDir;
    private ImageService images;

    @BeforeEach
    void setUp() throws Exception {
        MediaStorageService storage = mock(MediaStorageService.class);
        when(storage.resolvePath(anyString())).thenAnswer(invocation -> {
            Path path = tempDir.resolve(invocation.getArgument(0, String.class));
            Files.createDirectories(path.getParent());
            return path.toString();
        });
        when(storage.publicUrl(anyString())).thenAnswer(invocation ->
                "/media/" + invocation.getArgument(0, String.class));
        images = new ImageService(storage);
    }

    @Test void jpegIsStoredAsJpg() throws Exception { assertNormalized("jpg", MediaType.IMAGE_JPEG_VALUE, "post.jpg"); }
    @Test void pngIsStoredAsJpg() throws Exception { assertNormalized("png", MediaType.IMAGE_PNG_VALUE, "post.png"); }
    @Test void gifFirstFrameIsStoredAsJpg() throws Exception { assertNormalized("gif", MediaType.IMAGE_GIF_VALUE, "post.gif"); }
    @Test void bmpIsStoredAsJpg() throws Exception { assertNormalized("bmp", "image/bmp", "post.bmp"); }

    @Test
    void declaredMimeMustMatchActualImageContent() throws Exception {
        MockMultipartFile fakeJpeg = image("png", MediaType.IMAGE_JPEG_VALUE, "fake.jpg");
        assertThrows(IllegalArgumentException.class, () -> images.savePostImage(fakeJpeg, 1L));
        assertThat(postFiles()).isEmpty();
    }

    @Test
    void corruptImageIsRejectedWithoutAResidualFile() {
        MockMultipartFile corrupt = new MockMultipartFile("file", "corrupt.png", MediaType.IMAGE_PNG_VALUE,
                "not an image".getBytes());
        assertThrows(IllegalArgumentException.class, () -> images.savePostImage(corrupt, 1L));
        assertThat(postFiles()).isEmpty();
    }

    @Test
    void emptyImageIsRejected() {
        MockMultipartFile empty = new MockMultipartFile("file", "empty.png", MediaType.IMAGE_PNG_VALUE, new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> images.savePostImage(empty, 1L));
    }

    @Test
    void imageOverTenMegabytesIsRejected() {
        MockMultipartFile oversized = new MockMultipartFile("file", "large.png", MediaType.IMAGE_PNG_VALUE,
                new byte[10 * 1024 * 1024 + 1]);
        assertThrows(IllegalArgumentException.class, () -> images.savePostImage(oversized, 1L));
        assertThat(postFiles()).isEmpty();
    }

    private void assertNormalized(String format, String mime, String filename) throws Exception {
        String url = images.savePostImage(image(format, mime, filename), 1L);
        assertThat(url).startsWith("/media/posts/post_").endsWith(".jpg");
        assertThat(postFiles()).hasSize(1);
        assertThat(ImageIO.read(postFiles().get(0).toFile())).isNotNull();
    }

    private MockMultipartFile image(String format, String mime, String filename) throws Exception {
        BufferedImage image = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.BLUE.getRGB());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertThat(ImageIO.write(image, format, output)).isTrue();
        return new MockMultipartFile("file", filename, mime, output.toByteArray());
    }

    private java.util.List<Path> postFiles() {
        Path posts = tempDir.resolve("posts");
        if (!Files.exists(posts)) return java.util.List.of();
        try (var files = Files.list(posts)) {
            return files.toList();
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
