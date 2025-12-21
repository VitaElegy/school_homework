package com.school.homework.service;

import com.school.homework.dao.CommentRepository;
import com.school.homework.dao.PostRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.dto.CommentDto;
import com.school.homework.entity.Comment;
import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Post post;

    @BeforeEach
    public void setup() {
        user = new User(1L, "testuser", "password", "test@example.com", LocalDateTime.now(), null, new HashSet<>());
        post = new Post(1L, "Test Title", "Test Content", com.school.homework.enums.PostStatus.DRAFT, user, null, new HashSet<>());
    }

    @Test
    public void whenAddComment_thenReturnComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("Nice post!");

        Comment savedComment = new Comment();
        savedComment.setId(1L);
        savedComment.setContent("Nice post!");
        savedComment.setAuthor(user);
        savedComment.setPost(post);

        given(postRepository.findById(1L)).willReturn(Optional.of(post));
        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(commentRepository.save(any(Comment.class))).willReturn(savedComment);

        Comment addedComment = commentService.addComment(commentDto, 1L, "testuser");

        assertThat(addedComment).isNotNull();
        assertThat(addedComment.getPost()).isEqualTo(post);
        assertThat(addedComment.getAuthor()).isEqualTo(user);
        assertThat(addedComment.getContent()).isEqualTo("Nice post!");
    }
}
