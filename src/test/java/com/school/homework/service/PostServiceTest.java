package com.school.homework.service;

import com.school.homework.dao.PostRepository;
import com.school.homework.dao.TagRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.dto.PostDto;
import com.school.homework.entity.Post;
import com.school.homework.entity.Tag;
import com.school.homework.entity.User;
import com.school.homework.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;
    private PostDto postDto;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setStatus(com.school.homework.enums.PostStatus.DRAFT);
        post.setAuthor(user);
        
        postDto = new PostDto();
        postDto.setTitle("Test Title");
        postDto.setContent("Test Content");
        postDto.setStatus(com.school.homework.enums.PostStatus.DRAFT);
    }

    @Test
    public void whenCreatePost_thenReturnPost() {
        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

        Post createdPost = postService.createPost(postDto, "testuser");

        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getTitle()).isEqualTo("Test Title");
        assertThat(createdPost.getAuthor()).isEqualTo(user);
    }

    @Test
    public void whenCreatePostWithTags_thenReturnPostWithTags() {
        String tagString = "Java, Spring";
        postDto.setTagString(tagString);

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> invocation.getArgument(0));

        // Mocking for tags
        Tag springTag = new Tag("Spring");
        springTag.setId(11L);

        // Batch find
        given(tagRepository.findByNameIn(any())).willReturn(Arrays.asList(springTag)); // "Spring" exists

        // Batch save for "Java"
        given(tagRepository.saveAll(any())).willAnswer(invocation -> {
            List<Tag> tags = invocation.getArgument(0);
            tags.forEach(t -> t.setId(10L));
            return tags;
        });

        Post createdPost = postService.createPost(postDto, "testuser");

        assertThat(createdPost).isNotNull();
        assertThat(createdPost.getTags()).hasSize(2);
        assertThat(createdPost.getTags()).extracting("name").contains("Java", "Spring");
    }

    @Test
    public void whenGetAllPosts_thenReturnPostPage() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Post> page = new PageImpl<>(Arrays.asList(post));

        given(postRepository.findAll(pageable)).willReturn(page);

        Page<Post> result = postService.getAllPosts(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(post.getTitle());
    }

    @Test
    public void whenUpdatePost_thenUpdateTitleAndTags() {
        Long postId = 1L;
        PostDto updates = new PostDto();
        updates.setTitle("Updated Title");
        updates.setContent("Updated Content");
        String tags = "NewTag";
        updates.setTagString(tags);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(postRepository.save(any(Post.class))).willAnswer(i -> i.getArgument(0));

        // Mock tag batch
        given(tagRepository.findByNameIn(any())).willReturn(Collections.emptyList());
        given(tagRepository.saveAll(any())).willAnswer(i -> {
            List<Tag> t = i.getArgument(0);
            t.forEach(tag -> tag.setId(20L));
            return t;
        });

        Post updatedPost = postService.updatePost(postId, updates, "testuser");

        assertThat(updatedPost.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedPost.getContent()).isEqualTo("Updated Content");
        assertThat(updatedPost.getTags()).hasSize(1);
        assertThat(updatedPost.getTags().iterator().next().getName()).isEqualTo("NewTag");
    }
}
