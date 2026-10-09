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

    public boolean toggleLike(Post post, Video video, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Optional<Like> existingLike;

        if (post != null) {
            if (post.getAuthor() != null && post.getAuthor().getId().equals(user.getId())) {
                throw new SecurityException("Vous ne pouvez pas aimer votre propre publication");
            }
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
        throw new UnsupportedOperationException("Endpoint legacy Like désactivé");
    }

    public void deleteLike(Long id) {
        throw new UnsupportedOperationException("Endpoint legacy Like désactivé");
    }
}
