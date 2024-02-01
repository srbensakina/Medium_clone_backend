package com.api.medium_clone.service;


import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.repository.ArticleRepository;
import com.api.medium_clone.specifications.ArticleSpecifications;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    private final UserServiceImpl userService;

    private final ProfileServiceImpl profileService;


    private final ModelMapper modelMapper;


    @Override
    public ArticleListResponseDto listArticles(int limit, int offset, String tag, String author, String favorited) {
        Specification<Article> spec = Specification.where(ArticleSpecifications.orderByMostRecent());

        if (tag != null) {
            spec = spec.and(ArticleSpecifications.hasTag(tag));
        }

        if (author != null) {
            spec = spec.and(ArticleSpecifications.hasAuthor(author));
        }

        if (favorited != null) {
            spec = spec.and(ArticleSpecifications.isFavoritedBy(favorited));
        }

        List<Article> articles = articleRepository.findAll(spec, PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt")))).getContent();

        return ArticleListResponseDto.builder()
                .articles(articles.stream()
                        .map(article -> {
                            ArticleListResponseItemDto dto = modelMapper.map(article, ArticleListResponseItemDto.class);
                            dto.setAuthor(article.getAuthor().getUsername());
                            return dto;
                        })
                        .collect(Collectors.toList()))
                .articlesCount(articles.size())
                .build();


    }

    @Override
    public List<Article> getFeedArticles(UserDetails userDetails, int limit, int offset) {
        UserEntity currentUser = userService.getCurrentUser(userDetails.getUsername());
        List<UserEntity> followedUsers = profileService.getFollowedUsers(currentUser);
        followedUsers.add(currentUser);

        return articleRepository.findFeedArticlesByAuthors(followedUsers,
                PageRequest.of(offset, limit,
                        Sort.by(Sort.Order.desc("createdAt"))));
    }
}

