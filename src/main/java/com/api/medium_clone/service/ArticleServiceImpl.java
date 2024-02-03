package com.api.medium_clone.service;


import com.api.medium_clone.dto.ArticleCreateRequestDto;
import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.dto.UpdateArticleRequestDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.Tag;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.exception.ArticleAccessDeniedException;
import com.api.medium_clone.exception.ArticleNotFoundException;
import com.api.medium_clone.repository.ArticleRepository;
import com.api.medium_clone.repository.CommentRepository;
import com.api.medium_clone.repository.TagRepository;
import com.api.medium_clone.specifications.ArticleSpecifications;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    private final UserService userService;

    private final ProfileServiceImpl profileService;

    private final TagRepository tagRepository;

    private final CommentRepository commentRepository;

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
    public ArticleListResponseDto getFeedArticles(UserDetails userDetails, int limit, int offset) {
        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        List<UserEntity> followedUsers = profileService.getFollowedUsers(currentUser);
        followedUsers.add(currentUser);

        List<Article> feedArticles =  articleRepository.findFeedArticlesByAuthors(followedUsers,
                PageRequest.of(offset, limit,
                        Sort.by(Sort.Order.desc("createdAt"))));

        List<ArticleListResponseItemDto> articleListResponseItemDtos = feedArticles.stream()
                .map(article -> {
                    ArticleListResponseItemDto dto = modelMapper.map(article, ArticleListResponseItemDto.class);
                    dto.setAuthor(article.getAuthor().getUsername());
                    return dto;
                })
                .collect(Collectors.toList());

        return   ArticleListResponseDto.builder()
                .articles(articleListResponseItemDtos)
                .articlesCount(feedArticles.size())
                .build();

    }

    public Article findArticleBySlug(String slug) {
      return articleRepository.findBySlug(slug).orElseThrow(() -> new ArticleNotFoundException("Article not found"));
    }

    @Override
    public ArticleListResponseItemDto getArticleBySlug(String slug) {
       Article article = articleRepository.findBySlug(slug).orElseThrow(() -> new ArticleNotFoundException("Article not found"));

        ArticleListResponseItemDto articleListResponseItemDto = modelMapper.map(article, ArticleListResponseItemDto.class);
        articleListResponseItemDto.setAuthor(article.getAuthor().getUsername());

        return articleListResponseItemDto;
    }

    @Override
    public ArticleListResponseItemDto createArticle(ArticleCreateRequestDto createRequestDto, UserEntity author) {
        Article article = modelMapper.map(createRequestDto, Article.class);
        article.setAuthor(author);

        Set<Tag> tags =createTags( createRequestDto.getTagList());

        article.setTags(tags);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setSlug(generateSlug(createRequestDto.getTitle()));

        Article savedArticle = articleRepository.save(article);

        ArticleListResponseItemDto articleDto = modelMapper.map(savedArticle, ArticleListResponseItemDto.class);
        articleDto.setAuthor(article.getAuthor().getUsername());
        articleDto.setTagList(tags.stream().map(Tag::getName).collect(Collectors.toList()));
        return articleDto;
    }

    @Override
    public ArticleListResponseItemDto updateArticle(String slug, UserEntity currentUser, UpdateArticleRequestDto updateArticleRequestDto) {
        Article article = findArticleBySlug(slug);
        assert false;
        validateArticleOwnership(currentUser, article.getAuthor());

        updateArticleFields(article , updateArticleRequestDto);
        article.setSlug(generateSlug(updateArticleRequestDto.getTitle()));
        article.setUpdatedAt(LocalDateTime.now());

        Article updatedArticle = articleRepository.save(article);
        ArticleListResponseItemDto responseItemDto = modelMapper.map(updatedArticle, ArticleListResponseItemDto.class);
        responseItemDto.setDescription(article.getDescription());
        responseItemDto.setBody(article.getBody());
        responseItemDto.setTagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        responseItemDto.setAuthor(article.getAuthor().getUsername());
        return responseItemDto;
    }

    @Override
    public void deleteArticle(String slug, UserEntity currentUser) {
        Article article = findArticleBySlug(slug);
        assert false;
        validateArticleOwnership(currentUser, article.getAuthor());

        article.getTags().clear();
        article.getComments().clear();
        articleRepository.save(article);

        commentRepository.deleteAll(article.getComments());

        articleRepository.delete(article);

    }


    private void updateArticleFields(Article article, UpdateArticleRequestDto updateArticleRequestDto) {
        Optional.ofNullable(updateArticleRequestDto.getTitle())
                .ifPresent(article::setTitle);

        Optional.ofNullable(updateArticleRequestDto.getDescription())
                .ifPresent(article::setDescription);

        Optional.ofNullable(updateArticleRequestDto.getBody())
                .ifPresent(article::setBody);
    }


    private void validateArticleOwnership(UserEntity currentUser, UserEntity articleAuthor) {
        if (!currentUser.equals(articleAuthor)) {
            throw new ArticleAccessDeniedException("You don't have permission to update this article");
        }

    }

    public static String generateSlug(String title) {
        String lowercaseTitle = title.toLowerCase();
        String slug = lowercaseTitle.replaceAll("\\s", "-");
        slug = slug.replaceAll("[^a-zA-Z0-9-]", "");
        slug = slug.trim();

        return slug;
    }


    public Set<Tag> createTags(List<String> tags){
      return   tags.stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        }))
                .collect(Collectors.toSet());
    }





}


