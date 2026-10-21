package com.watyouface.service;

import com.watyouface.dto.UserDataExportDTO;
import com.watyouface.entity.*;
import com.watyouface.exception.ResourceNotFoundException;
import com.watyouface.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class UserDataExportService {
    private final UserRepository users;
    private final PostRepository posts;
    private final CommentRepository comments;
    private final LikeRepository likes;
    private final MessageRepository messages;
    private final ConversationUserRepository conversationUsers;
    private final ListingRepository listings;
    private final TransactionRepository transactions;
    private final WalletRepository wallets;
    private final VideoRepository videos;
    private final VideoShareRepository videoShares;
    private final UserContractRepository userContracts;

    public UserDataExportService(UserRepository users, PostRepository posts, CommentRepository comments,
                                 LikeRepository likes, MessageRepository messages,
                                 ConversationUserRepository conversationUsers, ListingRepository listings,
                                 TransactionRepository transactions, WalletRepository wallets,
                                 VideoRepository videos, VideoShareRepository videoShares,
                                 UserContractRepository userContracts) {
        this.users = users;
        this.posts = posts;
        this.comments = comments;
        this.likes = likes;
        this.messages = messages;
        this.conversationUsers = conversationUsers;
        this.listings = listings;
        this.transactions = transactions;
        this.wallets = wallets;
        this.videos = videos;
        this.videoShares = videoShares;
        this.userContracts = userContracts;
    }

    @Transactional(readOnly = true)
    public UserDataExportDTO exportFor(Long authenticatedUserId) {
        User user = users.findByIdAndDeletedAtIsNull(authenticatedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte actif introuvable"));

        List<UserDataExportDTO.ConversationData> conversations = conversationUsers.findByUser_Id(user.getId())
                .stream().map(ConversationUser::getConversation).distinct()
                .map(c -> new UserDataExportDTO.ConversationData(c.getId(), c.isGroup(), c.getTitle(), c.getCreatedAt()))
                .toList();

        List<UserDataExportDTO.ContractData> contracts = userContracts.findAllByUser_Id(user.getId()).stream()
                .map(uc -> new UserDataExportDTO.ContractData(
                        uc.getContract() == null ? null : uc.getContract().getId(),
                        uc.getContract() == null ? null : uc.getContract().getTitle(),
                        uc.getContract() == null ? null : uc.getContract().getVersion(),
                        uc.isAccepted(), uc.getAcceptedAt()))
                .toList();
        if (user.getAcceptedContractVersion() != null && contracts.stream().noneMatch(c ->
                user.getAcceptedContractVersion().getId().equals(c.contractId()))) {
            Contract contract = user.getAcceptedContractVersion();
            contracts = java.util.stream.Stream.concat(contracts.stream(), java.util.stream.Stream.of(
                    new UserDataExportDTO.ContractData(contract.getId(), contract.getTitle(), contract.getVersion(),
                            user.isAcceptedContract(), null))).toList();
        }

        return new UserDataExportDTO(
                "1.0", Instant.now(),
                new UserDataExportDTO.UserData(user.getId(), user.getUsername(), user.getEmail(),
                        user.getRole() == null ? null : user.getRole().name(), user.getAvatarUrl(),
                        user.isAcceptedContract(), user.getAcceptedContractVersion() == null
                        ? null : user.getAcceptedContractVersion().getVersion()),
                posts.findByAuthor_Id(user.getId()).stream()
                        .map(p -> new UserDataExportDTO.PostData(p.getId(), p.getContent(), p.getImageUrl(),
                                p.getVideoUrl(), p.getCreatedAt())).toList(),
                comments.findByAuthor_Id(user.getId()).stream()
                        .map(c -> new UserDataExportDTO.CommentData(c.getId(), c.getContent(), c.getCreatedAt(),
                                c.getPost() == null ? null : c.getPost().getId(),
                                c.getVideo() == null ? null : c.getVideo().getId())).toList(),
                likes.findByUser_Id(user.getId()).stream()
                        .map(l -> new UserDataExportDTO.LikeData(l.getId(),
                                l.getPost() == null ? null : l.getPost().getId(),
                                l.getVideo() == null ? null : l.getVideo().getId())).toList(),
                messages.findBySender_Id(user.getId()).stream()
                        .map(m -> new UserDataExportDTO.MessageData(m.getId(),
                                m.getConversation() == null ? null : m.getConversation().getId(),
                                m.getContent(), m.getSentAt(), m.isEdited(), m.isDeleted())).toList(),
                conversations,
                listings.findBySeller_IdOrBuyer_Id(user.getId(), user.getId()).stream()
                        .map(l -> new UserDataExportDTO.ListingData(l.getId(), l.getTitle(), l.getDescription(),
                                l.getPrice(), l.getImage(), l.getCreatedAt(), l.isHidden(),
                                l.getStatus() == null ? null : l.getStatus().name(),
                                l.getSeller() != null && user.getId().equals(l.getSeller().getId()) ? "SELLER" : "BUYER"))
                        .toList(),
                transactions.findByFromUser_IdOrToUser_Id(user.getId(), user.getId()).stream()
                        .map(t -> new UserDataExportDTO.TransactionData(t.getId(), t.getAmount(),
                                t.getStatus() == null ? null : t.getStatus().name(),
                                t.getListing() == null ? null : t.getListing().getId(), t.getCreatedAt(),
                                user.getId().equals(t.getFromUser().getId()) ? "BUYER" : "SELLER"))
                        .toList(),
                wallets.findByUser_Id(user.getId()).map(w -> new UserDataExportDTO.WalletData(
                        w.getBalance(), "Crédits de démonstration WatYouFace (sans valeur monétaire)"))
                        .orElse(null),
                videos.findByUploader_Id(user.getId()).stream()
                        .map(v -> new UserDataExportDTO.VideoData(v.getId(), v.getTitle(), v.getUrl(), v.getUploadedAt()))
                        .toList(),
                videoShares.findBySender_IdOrReceiver_Id(user.getId(), user.getId()).stream()
                        .map(s -> new UserDataExportDTO.VideoShareData(s.getId(),
                                s.getVideo() == null ? null : s.getVideo().getId(), s.isSeen(),
                                s.getSender() != null && user.getId().equals(s.getSender().getId()) ? "SENT" : "RECEIVED"))
                        .toList(),
                contracts);
    }
}
