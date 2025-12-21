package com.school.homework.dto;

import lombok.Data;

@Data
public class PostSearchCriteria {
    private String query;
    private String tag;
    private String authorUsername;
}

