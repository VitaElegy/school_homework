package com.school.homework.service;

import com.school.homework.dto.CommentDto;
import com.school.homework.entity.Comment;
import java.util.List;

public interface CommentService {
    Comment addComment(CommentDto commentDto, Long postId, String username);
    List<Comment> getCommentsByPostId(Long postId);
}
