package com.school.homework.service.impl;

import com.school.homework.constant.AppConstants;
import com.school.homework.dao.PostRepository;
import com.school.homework.dao.TagRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.dao.specification.PostSpecification;
import com.school.homework.dto.PostDto;
import com.school.homework.dto.PostSearchCriteria;
import com.school.homework.entity.Post;
import com.school.homework.entity.Tag;
import com.school.homework.entity.User;
import com.school.homework.exception.ResourceNotFoundException;
import com.school.homework.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 *
 * <p>实现文章相关的业务逻辑，包括文章的 CRUD 操作、搜索、标签处理等。</p>
 *
 * <p>主要功能：
 * <ul>
 *   <li>文章创建、更新、删除</li>
 *   <li>文章搜索和分页</li>
 *   <li>标签处理（批量查询优化，避免 N+1 问题）</li>
 *   <li>浏览次数原子递增</li>
 *   <li>权限检查（所有权验证）</li>
 * </ul>
 * </p>
 *
 * @author School Homework Team
 * @version 1.0
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {

    /** 文章数据访问接口 */
    private final PostRepository postRepository;

    /** 用户数据访问接口 */
    private final UserRepository userRepository;

    /** 标签数据访问接口 */
    private final TagRepository tagRepository;

    /**
     * 构造函数注入依赖
     *
     * @param postRepository 文章数据访问接口
     * @param userRepository 用户数据访问接口
     * @param tagRepository 标签数据访问接口
     */
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
    public Page<Post> searchPosts(PostSearchCriteria criteria, Pageable pageable) {
        return postRepository.findAll(PostSpecification.withCriteria(criteria), pageable);
    }

    // Deprecated methods removed or redirected if needed, but for now searchPosts replaces them.

    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
    }

    @Override
    public PostDto getPostDtoById(Long id) {
        Post post = getPostById(id);
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setStatus(post.getStatus());

        String tagString = post.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "));
        dto.setTagString(tagString);

        return dto;
    }

    @Override
    public Post createPost(PostDto postDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        if (postDto.getStatus() != null) {
            post.setStatus(postDto.getStatus());
        }
        post.setAuthor(user);

        processTags(post, postDto.getTagString());

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long id, PostDto postDto, String username) {
        Post existingPost = getPostById(id);

        // Ownership check
        if (!existingPost.getAuthor().getUsername().equals(username)) {
             throw new AccessDeniedException("You are not authorized to edit this post");
        }

        existingPost.setTitle(postDto.getTitle());
        existingPost.setContent(postDto.getContent());
        if (postDto.getStatus() != null) {
            existingPost.setStatus(postDto.getStatus());
        }
        existingPost.setUpdatedAt(LocalDateTime.now());

        // Clear existing tags and re-add to sync
        existingPost.getTags().clear();
        processTags(existingPost, postDto.getTagString());

        return postRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long id, String username) {
        Post post = getPostById(id);
        boolean isOwner = post.getAuthor().getUsername().equals(username);

        if (!isOwner) {
             User user = userRepository.findByUsername(username)
                     .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
             boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals(AppConstants.ROLE_ADMIN));
             if (!isAdmin) {
                 throw new AccessDeniedException("You are not authorized to delete this post");
             }
        }

        postRepository.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        // Atomic update to prevent race conditions
        postRepository.incrementViewCount(id);
    }

    private void processTags(Post post, String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return;
        }

        Set<String> tagNames = Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        if (tagNames.isEmpty()) {
            return;
        }

        // Batch fetch existing tags to avoid N+1 select problem
        List<Tag> existingTags = tagRepository.findByNameIn(tagNames);
        Map<String, Tag> existingTagMap = existingTags.stream()
                .collect(Collectors.toMap(Tag::getName, t -> t));

        List<Tag> tagsToAdd = new ArrayList<>();
        List<Tag> newTagsToSave = new ArrayList<>();

        for (String tagName : tagNames) {
            if (existingTagMap.containsKey(tagName)) {
                tagsToAdd.add(existingTagMap.get(tagName));
            } else {
                newTagsToSave.add(new Tag(tagName));
            }
        }

        if (!newTagsToSave.isEmpty()) {
            List<Tag> savedTags = tagRepository.saveAll(newTagsToSave);
            tagsToAdd.addAll(savedTags);
        }

        tagsToAdd.forEach(post::addTag);
    }
}
