package com.watyouface.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/** Stable, explicit export contract. Never serialize JPA entities for a user export. */
public record UserDataExportDTO(
        String formatVersion,
        Instant exportGeneratedAt,
        UserData user,
        List<PostData> posts,
        List<CommentData> comments,
        List<LikeData> likes,
        List<MessageData> messages,
        List<ConversationData> conversations,
        List<ListingData> listings,
        List<TransactionData> transactions,
        WalletData wallet,
        List<VideoData> videos,
        List<VideoShareData> videoShares,
        List<ContractData> contracts
) {
    public record UserData(Long id, String username, String email, String role, String avatarUrl,
                           boolean acceptedContract, String acceptedContractVersion) {}
    public record PostData(Long id, String content, String imageUrl, String videoUrl,
                           LocalDateTime createdAt) {}
    public record CommentData(Long id, String content, LocalDateTime createdAt,
                              Long postId, Long videoId) {}
    public record LikeData(Long id, Long postId, Long videoId) {}
    public record MessageData(Long id, Long conversationId, String content, Instant sentAt,
                              boolean edited, boolean deleted) {}
    public record ConversationData(Long id, boolean group, String title, Instant createdAt) {}
    public record ListingData(Long id, String title, String description, Double price, String image,
                              LocalDateTime createdAt, boolean hidden, String status, String relation) {}
    public record TransactionData(Long id, Double amount, String status, Long listingId,
                                 LocalDateTime createdAt, String relation) {}
    public record WalletData(Double balance, String currencyDescription) {}
    public record VideoData(Long id, String title, String url, LocalDateTime uploadedAt) {}
    public record VideoShareData(Long id, Long videoId, boolean seen, String direction) {}
    public record ContractData(Long contractId, String title, String version,
                               boolean accepted, LocalDateTime acceptedAt) {}
}
