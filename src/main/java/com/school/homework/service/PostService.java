package com.school.homework.service;

import com.school.homework.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    Page<Post> getAllPosts(Pageable pageable);
    Page<Post> searchPosts(String query, Pageable pageable);
    Post getPostById(Long id);
    Post createPost(Post post, Long userId, String tags);
    void deletePost(Long id, String username);
}

