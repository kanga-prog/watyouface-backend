package com.watyouface;

import com.watyouface.controller.ApiExceptionHandler;
import com.watyouface.controller.MarketplaceController;
import com.watyouface.media.MarketplaceImageService;
import com.watyouface.media.MediaStorageService;
import com.watyouface.security.Authz;
import com.watyouface.service.MarketplaceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MarketplaceImageUploadTests {

    private static final String UPLOAD_URL = "/api/marketplace/listings/upload";

    @TempDir
    Path tempDir;

    private MockMvc mvc;

    @BeforeEach
    void setUp() throws Exception {
        MediaStorageService storage = mock(MediaStorageService.class);
        when(storage.resolvePath(anyString())).thenAnswer(invocation -> {
            Path output = tempDir.resolve(invocation.getArgument(0, String.class));
            Files.createDirectories(output.getParent());
            return output.toString();
        });
        when(storage.publicUrl(anyString())).thenAnswer(invocation ->
                "/media/" + invocation.getArgument(0, String.class));

        MarketplaceImageService imageService = new MarketplaceImageService(storage);
        MarketplaceController controller = new MarketplaceController(
                mock(MarketplaceService.class), mock(Authz.class), imageService);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void acceptsJpegAndConvertsItToMarketplaceJpg() throws Exception {
        MockMultipartFile image = image("photo.jpeg", MediaType.IMAGE_JPEG_VALUE, "jpg");

        mvc.perform(multipart(UPLOAD_URL).file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.startsWith("/media/marketplace/listing_")))
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.endsWith(".jpg")));

        assertThat(marketplaceFiles()).hasSize(1);
        assertThat(ImageIO.read(marketplaceFiles().get(0).toFile())).isNotNull();
    }

    @Test
    void acceptsPngAndConvertsItToMarketplaceJpg() throws Exception {
        MockMultipartFile image = image("photo.png", MediaType.IMAGE_PNG_VALUE, "png");

        mvc.perform(multipart(UPLOAD_URL).file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.startsWith("/media/marketplace/listing_")))
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.endsWith(".jpg")));

        assertThat(marketplaceFiles()).hasSize(1);
        assertThat(ImageIO.read(marketplaceFiles().get(0).toFile())).isNotNull();
    }

    @Test
    void rejectsWebpWithBadRequest() throws Exception {
        assertBadRequest(new MockMultipartFile("file", "image.webp", "image/webp", "webp".getBytes()),
                "Format d'image non autorisé");
    }

    @Test
    void rejectsArbitraryMimeTypeWithBadRequest() throws Exception {
        assertBadRequest(new MockMultipartFile("file", "file.txt", MediaType.TEXT_PLAIN_VALUE, "text".getBytes()),
                "Format d'image non autorisé");
    }

    @Test
    void rejectsEmptyFileWithBadRequest() throws Exception {
        assertBadRequest(new MockMultipartFile("file", "empty.png", MediaType.IMAGE_PNG_VALUE, new byte[0]),
                "Image manquante ou vide");
    }

    @Test
    void rejectsFilesLargerThanTenMegabytesWithBadRequest() throws Exception {
        byte[] oversized = new byte[10 * 1024 * 1024 + 1];

        assertBadRequest(new MockMultipartFile("file", "large.png", MediaType.IMAGE_PNG_VALUE, oversized),
                "Image trop volumineuse : 10 Mo maximum.");
    }

    @Test
    void rejectsUnreadablePngContentWithBadRequestAndNoPartialFile() throws Exception {
        assertBadRequest(new MockMultipartFile("file", "invalid.png", MediaType.IMAGE_PNG_VALUE,
                        "not a PNG image".getBytes()),
                "Image invalide ou format non lisible");

        assertThat(marketplaceFiles()).isEmpty();
    }

    @Test
    void acceptsGifAndNormalizesItsFirstFrameToJpg() throws Exception {
        MockMultipartFile image = image("animated.gif", MediaType.IMAGE_GIF_VALUE, "gif");

        mvc.perform(multipart(UPLOAD_URL).file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.endsWith(".jpg")));

        assertThat(marketplaceFiles()).hasSize(1);
    }

    @Test
    void acceptsBmpAndNormalizesItToJpg() throws Exception {
        MockMultipartFile image = image("photo.bmp", "image/bmp", "bmp");

        mvc.perform(multipart(UPLOAD_URL).file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.endsWith(".jpg")));

        assertThat(marketplaceFiles()).hasSize(1);
    }

    @Test
    void rejectsImageWhoseBytesDoNotMatchItsDeclaredMimeType() throws Exception {
        MockMultipartFile pngClaimingToBeJpeg = image("mismatch.jpg", MediaType.IMAGE_JPEG_VALUE, "png");

        assertBadRequest(pngClaimingToBeJpeg, "Le contenu ne correspond pas au type MIME déclaré");
        assertThat(marketplaceFiles()).isEmpty();
    }

    private void assertBadRequest(MockMultipartFile file, String message) throws Exception {
        mvc.perform(multipart(UPLOAD_URL).file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(message));
    }

    private MockMultipartFile image(String filename, String contentType, String format) throws Exception {
        BufferedImage bufferedImage = new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0, 0, Color.BLUE.getRGB());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertThat(ImageIO.write(bufferedImage, format, output)).isTrue();
        return new MockMultipartFile("file", filename, contentType, output.toByteArray());
    }

    private java.util.List<Path> marketplaceFiles() throws Exception {
        Path marketplace = tempDir.resolve("marketplace");
        if (!Files.exists(marketplace)) {
            return java.util.List.of();
        }
        try (var files = Files.list(marketplace)) {
            return files.toList();
        }
    }
}
