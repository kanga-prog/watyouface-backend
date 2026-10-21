package com.watyouface.service;

import com.watyouface.entity.Comment;
import com.watyouface.entity.ConversationUser;
import com.watyouface.entity.Listing;
import com.watyouface.entity.Message;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.entity.enums.Role;
import com.watyouface.exception.ResourceNotFoundException;
import com.watyouface.media.MediaReferenceCleanupService;
import com.watyouface.repository.CommentRepository;
import com.watyouface.repository.ConversationRepository;
import com.watyouface.repository.ConversationUserRepository;
import com.watyouface.repository.LikeRepository;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.MessageRepository;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.TransactionRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.repository.VideoRepository;
import com.watyouface.repository.VideoShareRepository;
import com.watyouface.repository.WalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Performs GDPR-oriented account erasure without breaking immutable
 * Marketplace history. The User row becomes an irreversible anonymous
 * tombstone so existing foreign keys and completed transactions remain valid.
 */
@Service
public class AccountErasureService {

    private static final String DELETED_POST = "Publication supprimée";
    private static final String DELETED_COMMENT = "Commentaire supprimé";
    private static final String DELETED_MESSAGE = "Message supprimé par son auteur";
    private static final String DELETED_VIDEO = "Vidéo supprimée";
    private static final String DELETED_LISTING = "Annonce supprimée";

    private final UserRepository users;
    private final WalletRepository wallets;
    private final PostRepository posts;
    private final CommentRepository comments;
    private final LikeRepository likes;
    private final ConversationUserRepository conversationUsers;
    private final ConversationRepository conversations;
    private final MessageRepository messages;
    private final ListingRepository listings;
    private final TransactionRepository transactions;
    private final VideoRepository videos;
    private final VideoShareRepository videoShares;
    private final PasswordEncoder passwordEncoder;
    private final MediaReferenceCleanupService mediaCleanup;

    public AccountErasureService(UserRepository users,
                                 WalletRepository wallets,
                                 PostRepository posts,
                                 CommentRepository comments,
                                 LikeRepository likes,
                                 ConversationUserRepository conversationUsers,
                                 ConversationRepository conversations,
                                 MessageRepository messages,
                                 ListingRepository listings,
                                 TransactionRepository transactions,
                                 VideoRepository videos,
                                 VideoShareRepository videoShares,
                                 @Lazy PasswordEncoder passwordEncoder,
                                 MediaReferenceCleanupService mediaCleanup) {
        this.users = users;
        this.wallets = wallets;
        this.posts = posts;
        this.comments = comments;
        this.likes = likes;
        this.conversationUsers = conversationUsers;
        this.conversations = conversations;
        this.messages = messages;
        this.listings = listings;
        this.transactions = transactions;
        this.videos = videos;
        this.videoShares = videoShares;
        this.passwordEncoder = passwordEncoder;
        this.mediaCleanup = mediaCleanup;
    }

    @Transactional
    public void erase(Long userId) {
        User user = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        if (user.isDeleted()) return;

        Set<String> mediaCandidates = new LinkedHashSet<>();
        add(mediaCandidates, user.getAvatarUrl());

        scrubPosts(userId, mediaCandidates);
        deleteOwnCommentsAndLikes(userId);
        scrubVideosAndDeleteShares(userId, mediaCandidates);
        removeConversationMemberships(userId);
        scrubSentMessages(userId);
        preserveTransactionsAndHandleListings(userId, mediaCandidates);
        wallets.findByUser_Id(userId).ifPresent(wallets::delete);
        anonymizeIdentity(user);
        users.save(user);

        scheduleMediaCleanup(mediaCandidates);
    }

    private void scrubPosts(Long userId, Set<String> mediaCandidates) {
        List<Post> ownedPosts = posts.findByAuthor_Id(userId);
        for (Post post : ownedPosts) {
            add(mediaCandidates, post.getImageUrl());
            add(mediaCandidates, post.getVideoUrl());
            post.setContent(DELETED_POST);
            post.setImageUrl(null);
            post.setVideoUrl(null);
        }
        posts.saveAll(ownedPosts);
    }

    private void deleteOwnCommentsAndLikes(Long userId) {
        comments.deleteAll(comments.findByAuthor_Id(userId));
        likes.deleteAll(likes.findByUser_Id(userId));
    }

    private void scrubVideosAndDeleteShares(Long userId, Set<String> mediaCandidates) {
        List<Video> ownedVideos = videos.findByUploader_Id(userId);
        for (Video video : ownedVideos) {
            add(mediaCandidates, video.getUrl());
            video.setTitle(DELETED_VIDEO);
            video.setUrl(null);
        }
        videos.saveAll(ownedVideos);
        videoShares.deleteAll(videoShares.findBySender_IdOrReceiver_Id(userId, userId));
    }

    private void removeConversationMemberships(Long userId) {
        List<ConversationUser> memberships = conversationUsers.findByUser_Id(userId);
        Set<Long> conversationIds = memberships.stream()
                .map(membership -> membership.getConversation().getId())
                .collect(java.util.stream.Collectors.toSet());
        conversationUsers.deleteAll(memberships);
        conversationUsers.flush();

        for (Long conversationId : conversationIds) {
            if (conversationUsers.countByConversation_Id(conversationId) == 0) {
                messages.deleteByConversation_Id(conversationId);
                conversations.deleteById(conversationId);
            }
        }
    }

    private void scrubSentMessages(Long userId) {
        List<Message> sentMessages = messages.findBySender_Id(userId);
        for (Message message : sentMessages) {
            message.setContent(DELETED_MESSAGE);
            message.setDeleted(true);
        }
        messages.saveAll(sentMessages);
    }

    private void preserveTransactionsAndHandleListings(Long userId, Set<String> mediaCandidates) {
        for (Listing listing : listings.findBySeller_IdOrBuyer_Id(userId, userId)) {
            if (transactions.existsByListing_Id(listing.getId())) {
                add(mediaCandidates, listing.getImage());
                listing.setTitle(DELETED_LISTING);
                listing.setDescription(null);
                listing.setImage(null);
                listing.setHidden(true);
                listings.save(listing);
            } else {
                add(mediaCandidates, listing.getImage());
                listings.delete(listing);
            }
        }
    }

    private void anonymizeIdentity(User user) {
        String marker = UUID.randomUUID().toString();
        user.setUsername("deleted-" + marker);
        user.setEmail("deleted-" + marker + "@deleted.invalid");
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setAvatarUrl(null);
        user.setAcceptedContract(false);
        user.setAcceptedContractVersion(null);
        user.setRole(Role.USER);
        user.setDeletedAt(LocalDateTime.now());
    }

    private void scheduleMediaCleanup(Set<String> mediaCandidates) {
        if (mediaCandidates.isEmpty()) return;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                mediaCandidates.forEach(mediaCleanup::deleteIfUnreferenced);
            }
        });
    }

    private void add(Set<String> candidates, String publicUrl) {
        if (publicUrl != null && !publicUrl.isBlank()) candidates.add(publicUrl);
    }
}
