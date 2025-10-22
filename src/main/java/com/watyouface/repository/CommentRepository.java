package com.watyouface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.watyouface.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {}
