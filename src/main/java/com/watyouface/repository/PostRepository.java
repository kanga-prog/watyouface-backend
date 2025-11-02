package com.watyouface.repository;

import com.watyouface.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Option 1: JPA Query Method
    List<Post> findAllByOrderByCreatedAtDesc();

    // Option 2: JPQL Query (si besoin de plus de contrôle)
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllOrderedByDateDesc();
}
