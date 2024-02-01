package com.api.medium_clone.dto;

import com.api.medium_clone.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListResponseDto {

    private List<ArticleListResponseItemDto> articles;
    private int articlesCount;
}
