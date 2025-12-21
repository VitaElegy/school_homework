package com.school.homework.controller;

import com.school.homework.dao.PostRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.enums.PostStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BlogControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Post testPost;

    @BeforeEach
    public void setup() {
        // Since we are using an existing DB or H2, we might rely on DataInitializer or create our own.
        // Let's rely on finding 'user' created by DataInitializer, or create one if we want isolation.
        // DataInitializer creates 'admin' and 'user'.
        
        testUser = userRepository.findByUsername("user")
                .orElseThrow(() -> new RuntimeException("Test user not found"));

        testPost = new Post();
        testPost.setTitle("Integration Test Post");
        testPost.setContent("Content for integration test.");
        testPost.setAuthor(testUser);
        testPost.setStatus(PostStatus.DRAFT); // Default
        postRepository.save(testPost);
    }

    @Test
    public void shouldLoadBlogPage() throws Exception {
        mockMvc.perform(get("/blog"))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/posts"))
                .andExpect(content().string(containsString("Integration Test Post")));
    }

    @Test
    public void shouldLoadPostDetail() throws Exception {
        mockMvc.perform(get("/blog/posts/" + testPost.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("blog/post_detail"))
                .andExpect(content().string(containsString("Integration Test Post")));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"POST_CREATE"})
    public void shouldCreatePost() throws Exception {
        mockMvc.perform(post("/blog/posts")
                        .param("title", "New Created Post")
                        .param("content", "New Content")
                        .param("tagString", "Integration, Test")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/blog"));

        // Verify it exists in DB
        boolean exists = postRepository.findAll().stream()
                .anyMatch(p -> p.getTitle().equals("New Created Post"));
        assert(exists);
    }

    @Test
    public void shouldFailToCreatePostWithoutAuth() throws Exception {
        mockMvc.perform(post("/blog/posts")
                        .param("title", "Unauthorized Post")
                        .param("content", "Should fail")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection()) // Spring Security redirects to login
                .andExpect(redirectedUrlPattern("**/login"));
    }
}

