package com.watyouface.service;

import com.watyouface.entity.Comment;
import com.watyouface.repository.CommentRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Comment getCommentOrThrow(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire introuvable"));
    }

    private void assertOwnerOrAdmin(Comment comment, Long currentUserId, boolean isAdmin) {
        Long authorId = comment.getAuthor() != null ? comment.getAuthor().getId() : null;

        if (!isAdmin) {
            if (authorId == null || !authorId.equals(currentUserId)) {
                throw new AccessDeniedException("Interdit : action autorisée uniquement sur vos commentaires.");
            }
        }
    }

    // ✅ UPDATE sécurisé
    public Comment updateCommentAs(Long commentId, String newContent, Long currentUserId, boolean isAdmin) {
        Comment comment = getCommentOrThrow(commentId);

        assertOwnerOrAdmin(comment, currentUserId, isAdmin);

        String content = (newContent == null) ? "" : newContent.trim();
        if (content.isEmpty()) {
            throw new IllegalArgumentException("content ne doit pas être vide");
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    // ✅ DELETE sécurisé
    public void deleteCommentAs(Long commentId, Long currentUserId, boolean isAdmin) {
        Comment comment = getCommentOrThrow(commentId);

        assertOwnerOrAdmin(comment, currentUserId, isAdmin);

        commentRepository.deleteById(commentId);
    }
}
