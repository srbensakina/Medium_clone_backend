package com.api.medium_clone.controller;


import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<ArticleListResponseDto> listArticles(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String favorited) {

        ArticleListResponseDto articles = articleService.listArticles(limit, offset, tag, author, favorited);
        return ResponseEntity.ok(articles);
    }



    @GetMapping("/feed")
    public ResponseEntity<ArticleListResponseDto> getFeedArticles(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Article> feedArticles = articleService.getFeedArticles(userDetails, limit, offset);
        List<ArticleListResponseItemDto> articleListResponseItemDtos = feedArticles.stream()
                .map(article -> {
                    ArticleListResponseItemDto dto = modelMapper.map(article, ArticleListResponseItemDto.class);
                    dto.setAuthor(article.getAuthor().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ArticleListResponseDto.builder()
                .articles(articleListResponseItemDtos)
                .articlesCount(feedArticles.size())
                .build());
    }

}
