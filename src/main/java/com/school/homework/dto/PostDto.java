package com.school.homework.dto;

import com.school.homework.enums.PostStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentDto> comments;
    private Set<TagDto> tags;
}

