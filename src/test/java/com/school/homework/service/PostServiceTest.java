package com.school.homework.service;

import com.school.homework.dao.PostRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;

    @BeforeEach
    public void setup() {
        user = new User(1L, "testuser", "password", "test@example.com", LocalDateTime.now(), null, new HashSet<>());
        post = new Post(1L, "Test Title", "Test Content", com.school.homework.enums.PostStatus.DRAFT, user, null, new HashSet<>());
    }

    @Test
    public void whenCreatePost_thenReturnPost() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postRepository.save(post)).willReturn(post);

        Post createdPost = postService.createPost(post, 1L);

        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getTitle()).isEqualTo("Test Title");
        assertThat(createdPost.getAuthor()).isEqualTo(user);
    }

    @Test
    public void whenGetAllPosts_thenReturnPostList() {
        given(postRepository.findAll()).willReturn(Arrays.asList(post));

        List<Post> posts = postService.getAllPosts();

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo(post.getTitle());
    }
}

