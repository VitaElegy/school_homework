package com.school.homework.service;

import com.school.homework.dto.PostDto;
import com.school.homework.dto.PostSearchCriteria;
import com.school.homework.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<Post> getAllPosts(Pageable pageable);
    Page<Post> searchPosts(PostSearchCriteria criteria, Pageable pageable);
    Post getPostById(Long id);
    PostDto getPostDtoById(Long id);
    Post createPost(PostDto postDto, String username);
    Post updatePost(Long id, PostDto postDto, String username);
    void incrementViewCount(Long id);
    // Page<Post> getPostsByTag(String tagName, Pageable pageable); // Deprecated/Replaced by searchPosts
    void deletePost(Long id, String username);
}
