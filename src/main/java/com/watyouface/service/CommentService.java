package com.watyouface.service;

import com.watyouface.entity.Comment;
import com.watyouface.repository.CommentRepository;
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

    public List<Comment> getAllComments() { return commentRepository.findAll(); }

    public Comment createComment(Comment comment) { return commentRepository.save(comment); }

    public void deleteComment(Long id) { commentRepository.deleteById(id); }
}
