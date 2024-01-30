package com.api.medium_clone.dto;

import com.api.medium_clone.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListResponseDto {

    private List<Article> articles;
    private int articlesCount;
}
