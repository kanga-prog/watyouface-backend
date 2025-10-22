package com.watyouface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.watyouface.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {}
