package com.watyouface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.watyouface.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {}
