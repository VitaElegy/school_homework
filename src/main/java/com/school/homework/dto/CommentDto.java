package com.school.homework.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;

    @NotBlank(message = "Comment content cannot be empty")
    private String content;

    private String authorName;
    private LocalDateTime createdAt;
}

