package com.watyouface;

import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.media.MediaStorageService;
import com.watyouface.media.VideoService;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import com.watyouface.repository.VideoShareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VideoServiceAuthorizationTests {
    private VideoRepository videos;
    private VideoService service;
    private Video video;

    @BeforeEach
    void setUp() {
        videos = mock(VideoRepository.class);
        service = new VideoService(mock(MediaStorageService.class), videos, mock(UserRepository.class), mock(VideoShareRepository.class));
        User owner = new User(); owner.setId(1L);
        video = new Video(); video.setId(9L); video.setTitle("Original"); video.setUploader(owner);
        when(videos.findById(9L)).thenReturn(Optional.of(video));
        when(videos.save(any(Video.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test void ownerCanUpdateTitle() {
        assertEquals("Nouveau titre", service.updateTitleAs(9L, "Nouveau titre", 1L, false).getTitle());
    }

    @Test void anotherUserCannotUpdateTitle() {
        assertThrows(SecurityException.class, () -> service.updateTitleAs(9L, "Interdit", 2L, false));
    }

    @Test void ownerCanDelete() {
        assertDoesNotThrow(() -> service.deleteVideoAs(9L, 1L, false));
        verify(videos).delete(video);
    }

    @Test void anotherUserCannotDelete() {
        assertThrows(SecurityException.class, () -> service.deleteVideoAs(9L, 2L, false));
        verify(videos, never()).delete(any());
    }

    @Test void adminCanUpdateAndDelete() {
        assertDoesNotThrow(() -> service.updateTitleAs(9L, "Admin", 2L, true));
        assertDoesNotThrow(() -> service.deleteVideoAs(9L, 2L, true));
    }
}
