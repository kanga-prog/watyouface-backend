package com.watyouface.service;

import com.watyouface.dto.AdminCommentDTO;
import com.watyouface.dto.AdminListingDTO;
import com.watyouface.dto.AdminPostDTO;
import com.watyouface.dto.AdminUserDTO;
import com.watyouface.dto.WalletDTO;
import com.watyouface.entity.Comment;
import com.watyouface.entity.Listing;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.exception.ResourceNotFoundException;
import com.watyouface.repository.CommentRepository;
import com.watyouface.repository.ListingRepository;
import com.watyouface.repository.PostRepository;
import com.watyouface.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AdminModerationService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ListingRepository listingRepository;
    private final WalletService walletService;
    private final PostService postService;
    private final CommentService commentService;
    private final MarketplaceService marketplaceService;

    public AdminModerationService(UserRepository userRepository,
                                  PostRepository postRepository,
                                  CommentRepository commentRepository,
                                  ListingRepository listingRepository,
                                  WalletService walletService,
                                  PostService postService,
                                  CommentService commentService,
                                  MarketplaceService marketplaceService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.listingRepository = listingRepository;
        this.walletService = walletService;
        this.postService = postService;
        this.commentService = commentService;
        this.marketplaceService = marketplaceService;
    }

    public Page<AdminUserDTO> users(String query, int page, int size) {
        Pageable pageable = pageable(page, size, "id");
        String q = query == null ? "" : query.trim();
        Page<User> users = q.isBlank()
                ? userRepository.findAll(pageable)
                : userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q, pageable);
        return users.map(user -> new AdminUserDTO(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole().name(),
                user.isAcceptedContract(), walletService.findWallet(user.getId())
                        .map(wallet -> wallet.getBalance()).orElse(0.0)));
    }

    public Page<AdminPostDTO> posts(String query, int page, int size) {
        String q = query == null ? "" : query.trim();
        Page<Post> posts = q.isBlank()
                ? postRepository.findAll(pageable(page, size, "createdAt"))
                : postRepository.findByContentContainingIgnoreCase(q, pageable(page, size, "createdAt"));
        return posts.map(post -> new AdminPostDTO(
                post.getId(), post.getAuthor().getId(), post.getAuthor().getUsername(), post.getCreatedAt(),
                post.getContent(), post.getImageUrl(), post.getVideoUrl()));
    }

    public Page<AdminCommentDTO> comments(String query, int page, int size) {
        String q = query == null ? "" : query.trim();
        Page<Comment> comments = q.isBlank()
                ? commentRepository.findAll(pageable(page, size, "createdAt"))
                : commentRepository.findByContentContainingIgnoreCase(q, pageable(page, size, "createdAt"));
        return comments.map(comment -> new AdminCommentDTO(
                comment.getId(), comment.getAuthor().getId(), comment.getAuthor().getUsername(),
                comment.getPost() == null ? null : comment.getPost().getId(),
                comment.getPost() == null ? null : excerpt(comment.getPost().getContent()),
                comment.getContent(), comment.getCreatedAt()));
    }

    public Page<AdminListingDTO> listings(String query, int page, int size) {
        String q = query == null ? "" : query.trim();
        Page<Listing> listings = q.isBlank()
                ? listingRepository.findAll(pageable(page, size, "createdAt"))
                : listingRepository.findByTitleContainingIgnoreCase(q, pageable(page, size, "createdAt"));
        return listings.map(listing -> new AdminListingDTO(
                listing.getId(), listing.getTitle(), listing.getDescription(), listing.getSeller().getId(),
                listing.getSeller().getUsername(), listing.getPrice(), listing.getStatus().name(),
                listing.getImage(), listing.getCreatedAt(), listing.isHidden()));
    }

    @Transactional
    public WalletDTO creditWallet(Long userId, Double amount) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utilisateur introuvable");
        }
        return new WalletDTO(userId, walletService.credit(userId, amount).getBalance());
    }

    @Transactional
    public void deletePost(Long id) {
        postService.deletePostForModeration(id);
    }

    @Transactional
    public void deleteComment(Long id) {
        commentService.deleteCommentForModeration(id);
    }

    @Transactional
    public void deleteListing(Long id) {
        marketplaceService.deleteForModeration(id);
    }

    private Pageable pageable(int page, int size, String sort) {
        return PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100), Sort.by(Sort.Direction.DESC, sort));
    }

    private String excerpt(String content) {
        if (content == null) return "";
        return content.length() <= 160 ? content : content.substring(0, 157) + "…";
    }
}
