package com.api.medium_clone.service;

import com.api.medium_clone.dto.ArticleCreateRequestDto;
import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.dto.UpdateArticleRequestDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface ArticleService {

    ArticleListResponseDto listArticles(int limit, int offset, String tag, String author, String favorited) ;


    ArticleListResponseDto getFeedArticles(UserDetails userDetails, int limit, int offset);

    ArticleListResponseItemDto getArticleBySlug(String slug);

    ArticleListResponseItemDto createArticle(ArticleCreateRequestDto createRequestDto, UserEntity author);


    ArticleListResponseItemDto updateArticle(String slug, UserEntity currentUser, UpdateArticleRequestDto updateArticleRequestDto);

    void deleteArticle(String slug, UserEntity currentUser);


    ArticleListResponseItemDto favoriteArticle(String slug, UserEntity user);

    ArticleListResponseItemDto unfavoriteArticle(String slug, UserEntity currentUser);

    Article findArticleBySlug(String slug);
}
