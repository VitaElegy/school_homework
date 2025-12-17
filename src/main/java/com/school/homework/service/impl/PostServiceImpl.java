package com.school.homework.service.impl;

import com.school.homework.dao.PostRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> searchPosts(String query) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found: " + id));
    }

    @Override
    public Post createPost(Post post, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        post.setAuthor(user);
        // Status is set to DRAFT by default in Entity, or can be set by controller
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
