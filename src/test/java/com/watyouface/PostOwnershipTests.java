package com.watyouface;

import com.watyouface.entity.Post;
import com.watyouface.entity.User;
import com.watyouface.repository.PostRepository;
import com.watyouface.service.PostService;
import com.watyouface.service.UserService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostOwnershipTests {
    @Test
    void onlyOwnerOrAdminCanUpdateOrDeletePost() {
        PostRepository posts = mock(PostRepository.class);
        User owner = new User(); owner.setId(1L);
        Post post = new Post("original", owner);
        when(posts.findById(10L)).thenReturn(Optional.of(post));
        PostService service = new PostService(posts, mock(UserService.class));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.updatePostAs(10L, 2L, false, "unauthorized"));
        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> service.deletePostAs(10L, 2L, false));

        assertEquals("original", post.getContent());
        verify(posts, never()).deleteById(10L);

        service.updatePostAs(10L, 1L, false, "updated");
        assertEquals("updated", post.getContent());
        service.deletePostAs(10L, 3L, true);
        verify(posts).deleteById(10L);
    }
}
