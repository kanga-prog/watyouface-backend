package com.watyouface;

import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.repository.LikeRepository;
import com.watyouface.repository.UserRepository;
import com.watyouface.service.LikeService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LikeServiceAuthorizationTests {

    @Test
    void ownerCannotLikeOwnPost() {
        User owner = new User();
        owner.setId(10L);
        Post post = new Post();
        post.setAuthor(owner);
        UserRepository users = mock(UserRepository.class);
        when(users.findById(10L)).thenReturn(Optional.of(owner));

        LikeService service = new LikeService(mock(LikeRepository.class), users);

        assertThrows(SecurityException.class, () -> service.toggleLike(post, null, 10L));
    }
}
