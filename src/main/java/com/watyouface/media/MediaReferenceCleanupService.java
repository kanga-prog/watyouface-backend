package com.watyouface.media;

import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collection;

/**
 * Files are removed only after the database commit and only when no remaining
 * entity references their public URL. A failed file deletion is intentionally
 * non-destructive for database integrity and can be reconciled later.
 */
@Service
public class MediaReferenceCleanupService {

    private final UserRepository users;
    private final PostRepository posts;
    private final ListingRepository listings;
    private final VideoRepository videos;
    private final MediaStorageService storage;

    public MediaReferenceCleanupService(UserRepository users,
                                        PostRepository posts,
                                        ListingRepository listings,
                                        VideoRepository videos,
                                        MediaStorageService storage) {
        this.users = users;
        this.posts = posts;
        this.listings = listings;
        this.videos = videos;
        this.storage = storage;
    }

    public boolean deleteIfUnreferenced(String publicUrl) {
        if (publicUrl == null || publicUrl.isBlank() || isReferenced(publicUrl)) return false;
        return storage.deleteManagedPublicUrl(publicUrl);
    }

    /** Never remove a file before the database transaction which removed its
     * last reference has committed. */
    public void deleteIfUnreferencedAfterCommit(Collection<String> publicUrls) {
        if (publicUrls == null || publicUrls.isEmpty()) return;
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            publicUrls.forEach(this::deleteIfUnreferenced);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                publicUrls.forEach(MediaReferenceCleanupService.this::deleteIfUnreferenced);
            }
        });
    }

    private boolean isReferenced(String publicUrl) {
        return users.existsByAvatarUrl(publicUrl)
                || posts.existsByImageUrl(publicUrl)
                || posts.existsByVideoUrl(publicUrl)
                || listings.existsByImage(publicUrl)
                || videos.existsByUrl(publicUrl);
    }
}
