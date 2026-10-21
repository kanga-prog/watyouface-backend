package com.watyouface.media;

import com.watyouface.entity.Listing;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Reconciles database media URLs with a single configured media root. The
 * default method is non-destructive; purge is an explicit separate call.
 */
@Service
public class MediaReconciliationService {

    private static final Set<String> KNOWN_DIRECTORIES = Set.of("avatars", "posts", "videos", "marketplace");

    private final UserRepository users;
    private final PostRepository posts;
    private final ListingRepository listings;
    private final VideoRepository videos;
    private final MediaStorageService storage;

    public MediaReconciliationService(UserRepository users,
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

    public MediaReconciliationReport dryRun() {
        return reconcile(false);
    }

    /** Explicit destructive operation. This method is never called at startup
     * unless the operator separately enables and confirms the command. */
    public MediaReconciliationReport executePurge() {
        return reconcile(true);
    }

    private MediaReconciliationReport reconcile(boolean execute) {
        ReferenceSnapshot references = collectReferences();
        Path root = storage.getBasePath().toAbsolutePath().normalize();
        if (Files.isSymbolicLink(root)) {
            throw new IllegalStateException("Le media root ne peut pas être un lien symbolique");
        }
        if (!Files.exists(root)) {
            return new MediaReconciliationReport(!execute, 0, 0, 0, 0, 0,
                    references.invalidReferences, 0, 0, 0,
                    List.of("media root absent"), List.of());
        }
        if (!Files.isDirectory(root)) {
            throw new IllegalStateException("Le media root configuré n'est pas un répertoire");
        }

        List<String> anomalies = new ArrayList<>();
        List<MediaInventoryEntry> entries = new ArrayList<>();
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
                    String relative = root.relativize(file).toString().replace('\\', '/');
                    if (Files.isSymbolicLink(file)) {
                        entries.add(new MediaInventoryEntry(relative, extension(relative), 0, Instant.EPOCH,
                                MediaInventoryStatus.UNKNOWN));
                        anomalies.add("lien symbolique ignoré");
                        return FileVisitResult.CONTINUE;
                    }
                    if (!attributes.isRegularFile()) return FileVisitResult.CONTINUE;
                    MediaInventoryStatus status = classify(relative, references.urls);
                    entries.add(new MediaInventoryEntry(relative, extension(relative), attributes.size(),
                            attributes.lastModifiedTime().toInstant(), status));
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exception) {
                    anomalies.add("fichier inaccessible");
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException exception) {
            throw new IllegalStateException("Inventaire média impossible", exception);
        }

        int deleted = 0;
        if (execute) {
            for (MediaInventoryEntry entry : entries) {
                if (entry.status() == MediaInventoryStatus.ORPHAN
                        && storage.deleteManagedRelativePath(entry.relativePath())) {
                    deleted++;
                }
            }
        }

        int referenced = count(entries, MediaInventoryStatus.REFERENCED);
        int orphan = count(entries, MediaInventoryStatus.ORPHAN);
        int unknown = count(entries, MediaInventoryStatus.UNKNOWN);
        int protectedFiles = count(entries, MediaInventoryStatus.PROTECTED);
        long totalBytes = entries.stream().mapToLong(MediaInventoryEntry::sizeBytes).sum();
        long recoverable = entries.stream().filter(entry -> entry.status() == MediaInventoryStatus.ORPHAN)
                .mapToLong(MediaInventoryEntry::sizeBytes).sum();
        return new MediaReconciliationReport(!execute, entries.size(), referenced, orphan, unknown, protectedFiles,
                references.invalidReferences, totalBytes, recoverable, deleted, List.copyOf(anomalies), List.copyOf(entries));
    }

    private ReferenceSnapshot collectReferences() {
        Set<String> urls = new LinkedHashSet<>();
        int[] invalid = {0};
        users.findAll().forEach(user -> addReference(urls, invalid, user.getAvatarUrl()));
        posts.findAll().forEach(post -> {
            addReference(urls, invalid, post.getImageUrl());
            addReference(urls, invalid, post.getVideoUrl());
        });
        listings.findAll().forEach(listing -> addReference(urls, invalid, listing.getImage()));
        videos.findAll().forEach(video -> addReference(urls, invalid, video.getUrl()));
        return new ReferenceSnapshot(urls, invalid[0]);
    }

    private void addReference(Set<String> urls, int[] invalid, String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) return;
        Optional<String> normalized = storage.normalizeManagedPublicUrl(rawUrl);
        if (normalized.isPresent()) urls.add(normalized.get());
        else invalid[0]++;
    }

    private MediaInventoryStatus classify(String relative, Set<String> urls) {
        String directory = relative.contains("/") ? relative.substring(0, relative.indexOf('/')) : "";
        if (!KNOWN_DIRECTORIES.contains(directory)) return MediaInventoryStatus.UNKNOWN;
        if (relative.endsWith("/.keep") || relative.equals("avatars/default.png") || relative.equals("avatars/default.jpg")) {
            return MediaInventoryStatus.PROTECTED;
        }
        return urls.contains("/media/" + relative) ? MediaInventoryStatus.REFERENCED : MediaInventoryStatus.ORPHAN;
    }

    private int count(List<MediaInventoryEntry> entries, MediaInventoryStatus status) {
        return (int) entries.stream().filter(entry -> entry.status() == status).count();
    }

    private String extension(String relative) {
        int index = relative.lastIndexOf('.');
        return index < 0 ? "" : relative.substring(index + 1).toLowerCase(java.util.Locale.ROOT);
    }

    private record ReferenceSnapshot(Set<String> urls, int invalidReferences) { }
}
