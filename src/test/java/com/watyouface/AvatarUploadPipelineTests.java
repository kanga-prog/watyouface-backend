package com.watyouface;

import com.watyouface.exception.MediaUploadException;
import com.watyouface.media.AvatarService;
import com.watyouface.media.MediaStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Path;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvatarUploadPipelineTests {

    // Real 2x2 WebP generated locally with ffmpeg. It is intentionally kept as
    // bytes so this test has no ffmpeg or external-fixture dependency.
    private static final byte[] REAL_WEBP = Base64.getDecoder().decode(
            "UklGRjwAAABXRUJQVlA4IDAAAADQAQCdASoCAAIAAgA0JaACdLoB+AADsAD+8Oj3/yC5YXXI1/8gP+QH/ID/+PIAAAA=");

    @TempDir
    Path tempDir;

    @Test
    void pngAvatarIsCroppedAndWrittenAsJpeg() throws Exception {
        AvatarService avatars = new AvatarService(new MediaStorageService(tempDir.toString()));
        BufferedImage image = new BufferedImage(12, 8, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(3, 3, 0x80FF0000);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        assertThat(ImageIO.write(image, "png", output)).isTrue();

        String url = avatars.saveAvatar(new MockMultipartFile(
                "file", "avatar.png", MediaType.IMAGE_PNG_VALUE, output.toByteArray()), 42L);

        Path written = tempDir.resolve(url.substring("/media/".length()));
        assertThat(written).isRegularFile();
        BufferedImage jpeg = ImageIO.read(written.toFile());
        assertThat(jpeg).isNotNull();
        assertThat(jpeg.getWidth()).isEqualTo(256);
        assertThat(jpeg.getHeight()).isEqualTo(256);
    }

    @Test
    void webpIsRejectedBeforeDecodeUntilADecoderIsDelivered() {
        AvatarService avatars = new AvatarService(new MediaStorageService(tempDir.toString()));
        MockMultipartFile webp = new MockMultipartFile(
                "file", "avatar.webp", "image/webp", REAL_WEBP);

        MediaUploadException error = assertThrows(MediaUploadException.class, () -> avatars.saveAvatar(webp, 42L));
        assertThat(error.getStatus().value()).isEqualTo(415);
        assertThat(tempDir.resolve("avatars")).doesNotExist();
    }

    @Test
    void unreadablePngIsReportedAsUnprocessable() {
        AvatarService avatars = new AvatarService(new MediaStorageService(tempDir.toString()));
        MockMultipartFile corrupt = new MockMultipartFile(
                "file", "avatar.png", MediaType.IMAGE_PNG_VALUE, "not a PNG".getBytes());

        MediaUploadException error = assertThrows(MediaUploadException.class, () -> avatars.saveAvatar(corrupt, 42L));
        assertThat(error.getStatus().value()).isEqualTo(422);
    }
}
