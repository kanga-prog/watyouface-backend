package com.watyouface.service;

import com.watyouface.entity.Like;
import com.watyouface.repository.LikeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public List<Like> getAllLikes() { return likeRepository.findAll(); }

    public Like createLike(Like like) { return likeRepository.save(like); }

    public void deleteLike(Long id) { likeRepository.deleteById(id); }
}
