package com.school.homework.service;

import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.entity.Comment;
import java.util.List;

public interface BlogService {
    // User operations
    User registerUser(User user);
    User findUserByUsername(String username);

    // Post operations
    List<Post> getAllPosts();
    List<Post> searchPosts(String query);
    Post getPostById(Long id);
    Post createPost(Post post, Long userId);
    void deletePost(Long id);

    // Comment operations
    Comment addComment(Comment comment, Long postId, Long userId);
    List<Comment> getCommentsByPostId(Long postId);
}

