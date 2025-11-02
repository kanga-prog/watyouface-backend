package com.watyouface.repository;

import com.watyouface.entity.Like;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByPostAndUser(Post post, User user);

    Optional<Like> findByVideoAndUser(Video video, User user);
}
