package com.school.homework.service;

import com.school.homework.dao.CommentRepository;
import com.school.homework.dao.PostRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Comment;
import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.service.impl.BlogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private BlogServiceImpl blogService;

    private User user;
    private Post post;

    @BeforeEach
    public void setup() {
        user = new User(1L, "testuser", "password", "test@example.com", LocalDateTime.now(), null);
        post = new Post(1L, "Test Title", "Test Content", LocalDateTime.now(), LocalDateTime.now(), user, null);
    }

    @Test
    public void whenRegisterUser_thenReturnUser() {
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.empty());
        given(userRepository.save(user)).willReturn(user);

        User createdUser = blogService.registerUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void whenRegisterExistingUser_thenThrowException() {
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> blogService.registerUser(user));
    }

    @Test
    public void whenCreatePost_thenReturnPost() {
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(postRepository.save(post)).willReturn(post);

        Post createdPost = blogService.createPost(post, 1L);

        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getTitle()).isEqualTo("Test Title");
        assertThat(createdPost.getAuthor()).isEqualTo(user);
    }

    @Test
    public void whenAddComment_thenReturnComment() {
        Comment comment = new Comment(1L, "Nice post!", LocalDateTime.now(), null, null);

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(commentRepository.save(comment)).willReturn(comment);

        Comment addedComment = blogService.addComment(comment, 1L, 1L);

        assertThat(addedComment).isNotNull();
        assertThat(addedComment.getPost()).isEqualTo(post);
        assertThat(addedComment.getAuthor()).isEqualTo(user);
    }

    @Test
    public void whenGetAllPosts_thenReturnPostList() {
        given(postRepository.findAll()).willReturn(Arrays.asList(post));

        List<Post> posts = blogService.getAllPosts();

        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo(post.getTitle());
    }
}

