package com.school.homework.service.impl;

import com.school.homework.dao.PostRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.dto.CommentDto;
import com.school.homework.dto.PostDto;
import com.school.homework.dto.TagDto;
import com.school.homework.entity.Post;
import com.school.homework.entity.Tag;
import com.school.homework.entity.User;
import com.school.homework.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> searchPosts(String query) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(query, query).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found: " + id));
        return convertToDto(post);
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

    private PostDto convertToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setStatus(post.getStatus());
        if (post.getAuthor() != null) {
            dto.setAuthorName(post.getAuthor().getUsername());
        }
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        if (post.getComments() != null) {
            List<CommentDto> commentDtos = post.getComments().stream()
                    .map(comment -> {
                        CommentDto cDto = new CommentDto();
                        cDto.setId(comment.getId());
                        cDto.setContent(comment.getContent());
                        cDto.setCreatedAt(comment.getCreatedAt());
                        if (comment.getAuthor() != null) {
                            cDto.setAuthorName(comment.getAuthor().getUsername());
                        }
                        return cDto;
                    })
                    .collect(Collectors.toList());
            dto.setComments(commentDtos);
        }

        if (post.getTags() != null) {
            Set<TagDto> tagDtos = post.getTags().stream()
                    .map(tag -> {
                        TagDto tDto = new TagDto();
                        tDto.setId(tag.getId());
                        tDto.setName(tag.getName());
                        return tDto;
                    })
                    .collect(Collectors.toSet());
            dto.setTags(tagDtos);
        }

        return dto;
    }
}


