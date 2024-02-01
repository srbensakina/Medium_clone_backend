package com.api.medium_clone.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleListResponseItemDto {

    private Long id;
    private String slug;
    private String title;
    private String description;
    private String body;
    private List<String> tagList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean favorited;
    private int favoritesCount;
    private String author;
}
