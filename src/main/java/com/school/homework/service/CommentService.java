package com.school.homework.service;

import com.school.homework.dto.CommentDto;
import com.school.homework.entity.Comment;
import java.util.List;

public interface CommentService {
    Comment addComment(Comment comment, Long postId, Long userId);
    List<Comment> getCommentsByPostId(Long postId);
}

