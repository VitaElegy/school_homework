package com.school.homework.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;
}

