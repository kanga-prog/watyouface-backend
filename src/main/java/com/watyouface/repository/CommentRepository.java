package com.watyouface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.watyouface.entity.Comment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    Page<Comment> findByContentContainingIgnoreCase(String content, Pageable pageable);
    List<Comment> findByAuthor_Id(Long authorId);
}
