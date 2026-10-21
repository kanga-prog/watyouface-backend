package com.watyouface;

import com.watyouface.entity.Listing;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.media.MediaInventoryStatus;
import com.watyouface.media.MediaReconciliationReport;
import com.watyouface.media.MediaReconciliationService;
import com.watyouface.media.MediaStorageService;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MediaReconciliationServiceTests {

    @TempDir Path root;

    private UserRepository users;
    private PostRepository posts;
    private ListingRepository listings;
    private VideoRepository videos;
    private MediaStorageService storage;
    private MediaReconciliationService reconciliation;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        posts = mock(PostRepository.class);
        listings = mock(ListingRepository.class);
        videos = mock(VideoRepository.class);
        when(users.findAll()).thenReturn(List.of());
        when(posts.findAll()).thenReturn(List.of());
        when(listings.findAll()).thenReturn(List.of());
        when(videos.findAll()).thenReturn(List.of());
        storage = new MediaStorageService(root.toString());
        reconciliation = new MediaReconciliationService(users, posts, listings, videos, storage);
    }

    @Test
    void referencedFileIsNeverPurged() throws Exception {
        User user = new User();
        user.setAvatarUrl("/media/avatars/referenced.jpg");
        when(users.findAll()).thenReturn(List.of(user));
        Path file = write("avatars/referenced.jpg");

        MediaReconciliationReport report = reconciliation.executePurge();

        assertThat(report.referencedFiles()).isEqualTo(1);
        assertThat(report.filesDeleted()).isZero();
        assertThat(file).exists();
    }

    @Test
    void dryRunIdentifiesOrphanWithoutDeletingIt() throws Exception {
        Path orphan = write("posts/orphan.jpg");

        MediaReconciliationReport report = reconciliation.dryRun();

        assertThat(report.dryRun()).isTrue();
        assertThat(report.orphanFiles()).isEqualTo(1);
        assertThat(report.recoverableBytes()).isEqualTo(Files.size(orphan));
        assertThat(orphan).exists();
    }

    @Test
    void explicitPurgeDeletesOnlyOrphan() throws Exception {
        Path orphan = write("marketplace/unused.jpg");

        MediaReconciliationReport report = reconciliation.executePurge();

        assertThat(report.dryRun()).isFalse();
        assertThat(report.filesDeleted()).isEqualTo(1);
        assertThat(orphan).doesNotExist();
    }

    @Test
    void sharedReferenceIsKeptOnceForAllEntities() throws Exception {
        Post post = new Post();
        post.setImageUrl("/media/posts/shared.jpg");
        Listing listing = new Listing();
        listing.setImage("/media/posts/shared.jpg");
        when(posts.findAll()).thenReturn(List.of(post));
        when(listings.findAll()).thenReturn(List.of(listing));
        Path shared = write("posts/shared.jpg");

        MediaReconciliationReport report = reconciliation.executePurge();

        assertThat(report.referencedFiles()).isEqualTo(1);
        assertThat(report.filesDeleted()).isZero();
        assertThat(shared).exists();
    }

    @Test
    void missingFileAndAlreadyAbsentFileDoNotFailInventoryOrPurge() {
        Video video = new Video();
        video.setUrl("/media/videos/already-absent.mp4");
        when(videos.findAll()).thenReturn(List.of(video));

        MediaReconciliationReport dryRun = reconciliation.dryRun();
        MediaReconciliationReport purge = reconciliation.executePurge();

        assertThat(dryRun.invalidDatabaseReferences()).isZero();
        assertThat(dryRun.filesScanned()).isZero();
        assertThat(purge.filesDeleted()).isZero();
    }

    @Test
    void traversalReferenceIsRejectedAndCannotLeadOutsideMediaRoot() throws Exception {
        User user = new User();
        user.setAvatarUrl("/media/avatars/../../outside.jpg");
        when(users.findAll()).thenReturn(List.of(user));
        Path outside = root.getParent().resolve("outside.jpg");
        Files.writeString(outside, "outside");

        MediaReconciliationReport report = reconciliation.executePurge();

        assertThat(report.invalidDatabaseReferences()).isEqualTo(1);
        assertThat(outside).exists();
        Files.deleteIfExists(outside);
    }

    @Test
    void unknownAndProtectedFilesAreNotPurgeCandidates() throws Exception {
        Path protectedFile = write("avatars/default.png");
        Path unknown = write("legacy/old.bin");

        MediaReconciliationReport report = reconciliation.executePurge();

        assertThat(report.protectedFiles()).isEqualTo(1);
        assertThat(report.unknownFiles()).isEqualTo(1);
        assertThat(report.filesDeleted()).isZero();
        assertThat(report.entries()).extracting(entry -> entry.status())
                .containsExactlyInAnyOrder(MediaInventoryStatus.PROTECTED, MediaInventoryStatus.UNKNOWN);
        assertThat(protectedFile).exists();
        assertThat(unknown).exists();
    }

    private Path write(String relative) throws Exception {
        Path file = Path.of(storage.resolvePath(relative));
        Files.writeString(file, "test-media");
        return file;
    }
}
