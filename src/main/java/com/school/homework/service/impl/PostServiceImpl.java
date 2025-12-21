package com.school.homework.service.impl;

import com.school.homework.dao.PostRepository;
import com.school.homework.dao.TagRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Post;
import com.school.homework.entity.Tag;
import com.school.homework.entity.User;
import com.school.homework.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public Page<Post> searchPosts(String query, Pageable pageable) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query, pageable);
    }

    @Override
    public Page<Post> getPostsByTag(String tagName, Pageable pageable) {
        return postRepository.findByTags_Name(tagName, pageable);
    }

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found: " + id));
    }

    @Override
    public Post createPost(Post post, Long userId, String tags) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        post.setAuthor(user);

        processTags(post, tags);

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long id, Post postUpdates, String tags, String username) {
        Post existingPost = getPostById(id);

        // Ownership check
        if (!existingPost.getAuthor().getUsername().equals(username)) {
            // Check for admin role if we had easy access, or rely on controller security
             throw new AccessDeniedException("You are not authorized to edit this post");
        }

        existingPost.setTitle(postUpdates.getTitle());
        existingPost.setContent(postUpdates.getContent());
        existingPost.setUpdatedAt(LocalDateTime.now());

        // Clear existing tags and re-add to sync
        existingPost.getTags().clear();
        processTags(existingPost, tags);

        return postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long id, String username) {
        Post post = getPostById(id);
        boolean isOwner = post.getAuthor().getUsername().equals(username);

        if (!isOwner) {
             User user = userRepository.findByUsername(username).orElseThrow();
             boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
             if (!isAdmin) {
                 throw new AccessDeniedException("You are not authorized to delete this post");
             }
        }

        postRepository.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        Post post = getPostById(id);
        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);
    }
    
    private void processTags(Post post, String tags) {
        if (tags != null && !tags.trim().isEmpty()) {
            String[] tagNames = tags.split(",");
            for (String tagName : tagNames) {
                String cleanName = tagName.trim();
                if (!cleanName.isEmpty()) {
                    Tag tag = tagRepository.findByName(cleanName)
                            .orElseGet(() -> tagRepository.save(new Tag(cleanName)));
                    post.addTag(tag);
                }
            }
        }
    }
}
