package com.api.medium_clone.service;

import com.api.medium_clone.dto.ArticleCreateRequestDto;
import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.dto.UpdateArticleRequestDto;
import com.api.medium_clone.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface TagService {

    public List<String> getAllTags() ;
}
