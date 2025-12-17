package com.school.homework.service;

import com.school.homework.dto.PostDto;
import com.school.homework.entity.Post;
import java.util.List;

public interface PostService {
    List<PostDto> getAllPosts();
    List<PostDto> searchPosts(String query);
    PostDto getPostById(Long id);
    Post createPost(Post post, Long userId); // Keeping Entity for input for now
    void deletePost(Long id);
}

