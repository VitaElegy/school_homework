package com.school.homework.service.impl;

import com.school.homework.dao.CommentRepository;
import com.school.homework.dao.PostRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Comment;
import com.school.homework.entity.Post;
import com.school.homework.entity.User;
import com.school.homework.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Comment addComment(Comment comment, Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        comment.setPost(post);
        comment.setAuthor(user);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }
}
