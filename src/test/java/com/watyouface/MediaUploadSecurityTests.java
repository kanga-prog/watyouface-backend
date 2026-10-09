package com.watyouface;

import com.watyouface.media.ImageService;
import com.watyouface.media.MediaStorageService;
import com.watyouface.repository.PostRepository;
import com.watyouface.service.PostService;
import com.watyouface.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MediaUploadSecurityTests {

    @Test
    void postUploadRejectsUnapprovedMimeTypeBeforePersistingPost() {
        PostRepository posts = mock(PostRepository.class);
        PostService service = new PostService(posts, mock(UserService.class));
        MockMultipartFile pdf = new MockMultipartFile(
                "file", "payload.pdf", "application/pdf", "not an image".getBytes());

        assertThrows(IllegalArgumentException.class, () -> service.createPost(null, pdf));
        verify(posts, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void imageServiceRejectsUnapprovedMimeTypeBeforeWritingFile() {
        MediaStorageService storage = mock(MediaStorageService.class);
        ImageService images = new ImageService(storage);
        MockMultipartFile pdf = new MockMultipartFile(
                "file", "payload.pdf", "application/pdf", "not an image".getBytes());

        assertThrows(IllegalArgumentException.class, () -> images.savePostImage(pdf, 1L));
        verify(storage, never()).resolvePath(org.mockito.ArgumentMatchers.anyString());
    }
}
