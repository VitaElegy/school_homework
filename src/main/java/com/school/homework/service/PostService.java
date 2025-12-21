package com.school.homework.service;

import com.school.homework.dto.PostDto;
import com.school.homework.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<Post> getAllPosts(Pageable pageable);
    Page<Post> searchPosts(String query, Pageable pageable);
    Post getPostById(Long id);
    Post createPost(PostDto postDto, String username);
    Post updatePost(Long id, PostDto postDto, String username);
    PostDto getPostDtoById(Long id);
    void incrementViewCount(Long id);
    Page<Post> getPostsByTag(String tagName, Pageable pageable);
    void deletePost(Long id, String username);
}
