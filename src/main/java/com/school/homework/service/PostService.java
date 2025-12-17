package com.school.homework.service;

import com.school.homework.dto.PostDto;
import com.school.homework.entity.Post;
import java.util.List;

public interface PostService {
    List<Post> getAllPosts();
    List<Post> searchPosts(String query);
    Post getPostById(Long id);
    Post createPost(Post post, Long userId);
    void deletePost(Long id);
}

