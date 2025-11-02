package com.watyouface.service;

import com.watyouface.entity.Like;
import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.entity.Video;
import com.watyouface.repository.LikeRepository;
import com.watyouface.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
    }

    public boolean toggleLike(Post post, Video video, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Optional<Like> existingLike;

        if (post != null) {
            existingLike = likeRepository.findByPostAndUser(post, user);
        } else if (video != null) {
            existingLike = likeRepository.findByVideoAndUser(video, user);
        } else {
            throw new IllegalArgumentException("Post ou Video doit être fourni");
        }

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // like supprimé
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            like.setVideo(video);
            likeRepository.save(like);
            return true; // nouveau like ajouté
        }
    }

    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    public Like createLike(Like like) {
        return likeRepository.save(like);
    }

    public void deleteLike(Long id) {
        likeRepository.deleteById(id);
    }
}
