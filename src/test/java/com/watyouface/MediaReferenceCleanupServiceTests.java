package com.watyouface;

import com.watyouface.entity.Post;
import com.watyouface.media.MediaReferenceCleanupService;
import com.watyouface.media.MediaStorageService;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MediaReferenceCleanupServiceTests {

    @TempDir Path root;
    private MediaStorageService storage;
    private UserRepository users;
    private PostRepository posts;
    private ListingRepository listings;
    private VideoRepository videos;
    private MediaReferenceCleanupService cleanup;

    @BeforeEach
    void setUp() {
        storage = new MediaStorageService(root.toString());
        users = mock(UserRepository.class);
        posts = mock(PostRepository.class);
        listings = mock(ListingRepository.class);
        videos = mock(VideoRepository.class);
        when(users.existsByAvatarUrl(org.mockito.ArgumentMatchers.anyString())).thenReturn(false);
        when(posts.existsByImageUrl(org.mockito.ArgumentMatchers.anyString())).thenReturn(false);
        when(posts.existsByVideoUrl(org.mockito.ArgumentMatchers.anyString())).thenReturn(false);
        when(listings.existsByImage(org.mockito.ArgumentMatchers.anyString())).thenReturn(false);
        when(videos.existsByUrl(org.mockito.ArgumentMatchers.anyString())).thenReturn(false);
        cleanup = new MediaReferenceCleanupService(users, posts, listings, videos, storage);
    }

    @Test
    void replacementCandidateIsDeletedOnlyWhenNoEntityStillReferencesIt() throws Exception {
        Path old = write("avatars/old.jpg");
        Post shared = new Post();
        shared.setImageUrl("/media/avatars/old.jpg");
        when(posts.existsByImageUrl("/media/avatars/old.jpg")).thenReturn(true);

        assertThat(cleanup.deleteIfUnreferenced("/media/avatars/old.jpg")).isFalse();
        assertThat(old).exists();

        when(posts.existsByImageUrl("/media/avatars/old.jpg")).thenReturn(false);
        assertThat(cleanup.deleteIfUnreferenced("/media/avatars/old.jpg")).isTrue();
        assertThat(old).doesNotExist();
    }

    @Test
    void rollbackDoesNotDeleteCandidateBeforeDatabaseCommit() throws Exception {
        Path candidate = write("posts/rollback.jpg");
        TransactionSynchronizationManager.initSynchronization();
        try {
            cleanup.deleteIfUnreferencedAfterCommit(List.of("/media/posts/rollback.jpg"));
            assertThat(candidate).exists();
            TransactionSynchronizationManager.getSynchronizations()
                    .forEach(sync -> sync.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
        assertThat(candidate).exists();
    }

    @Test
    void commitRunsDeferredCleanupAndTraversalIsRefused() throws Exception {
        Path candidate = write("marketplace/commit.jpg");
        Path outside = root.getParent().resolve("cleanup-outside.jpg");
        Files.writeString(outside, "outside");
        TransactionSynchronizationManager.initSynchronization();
        try {
            cleanup.deleteIfUnreferencedAfterCommit(List.of("/media/marketplace/commit.jpg"));
            TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
        assertThat(candidate).doesNotExist();
        assertThat(cleanup.deleteIfUnreferenced("/media/posts/../../cleanup-outside.jpg")).isFalse();
        assertThat(outside).exists();
        Files.deleteIfExists(outside);
    }

    private Path write(String relative) throws Exception {
        Path file = Path.of(storage.resolvePath(relative));
        Files.writeString(file, "media");
        return file;
    }
}
