package com.api.medium_clone.service;

import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.entity.Article;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface ArticleService {

    ArticleListResponseDto listArticles(int limit, int offset, String tag, String author, String favorited) ;


    List<Article> getFeedArticles(UserDetails userDetails, int limit, int offset);




}
