package com.api.medium_clone.service;


import com.api.medium_clone.dto.ArticleCreateRequestDto;
import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.dto.UpdateArticleRequestDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.Tag;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.exception.ArticleAlreadyFavoritedException;
import com.api.medium_clone.exception.ArticleNotFoundException;
import com.api.medium_clone.repository.ArticleRepository;
import com.api.medium_clone.repository.CommentRepository;
import com.api.medium_clone.repository.TagRepository;
import com.api.medium_clone.repository.UserRepository;
import com.api.medium_clone.specifications.ArticleSpecifications;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.api.medium_clone.util.ArticleMapper.*;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    private final UserService userService;

    private final ProfileServiceImpl profileService;

    private final TagRepository tagRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final ModelMapper modelMapper;


   private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);


   @Override
    public ArticleListResponseDto listArticles(int limit, int offset, String tag, String author, String favorited) {
        logger.info("Listing articles with limit {}, offset {}, tag {}, author {}, favorited {}", limit, offset, tag, author, favorited);

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

        return ArticleListResponseDto.builder().articles(articles.stream().map(article -> {
            ArticleListResponseItemDto dto = modelMapper.map(article, ArticleListResponseItemDto.class);
            dto.setTagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()));

            mapAuthorAndFavorited(article, dto);

            logger.info("Found {} articles matching the criteria", articles.size());

            return dto;
        }).collect(Collectors.toList())).articlesCount(articles.size()).build();


    }

    @Override
    public ArticleListResponseDto getFeedArticles(UserDetails userDetails, int limit, int offset) {
        String username = userDetails.getUsername();
        logger.info("Fetching feed articles for user '{}'", username);

        UserEntity currentUser = userService.getUserByUsername(username);
        List<UserEntity> followedUsers = profileService.getFollowedUsers(currentUser);
        followedUsers.add(currentUser);

        List<Article> feedArticles = articleRepository.findFeedArticlesByAuthors(followedUsers, PageRequest.of(offset, limit, Sort.by(Sort.Order.desc("createdAt"))));
        logger.info("Found {} feed articles for user '{}'", feedArticles.size(), username);

        List<ArticleListResponseItemDto> articleListResponseItemDtos = feedArticles.stream().map(article -> {
            ArticleListResponseItemDto dto = modelMapper.map(article, ArticleListResponseItemDto.class);
            dto.setTagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
            mapAuthorAndFavorited(article, dto);
            return dto;
        }).collect(Collectors.toList());

        return ArticleListResponseDto.builder()
                .articles(articleListResponseItemDtos)
                .articlesCount(feedArticles.size())
                .build();
    }

     @Override
     public Article findArticleBySlug(String slug) {
         Optional<Article> optionalArticle = articleRepository.findBySlug(slug);
         if (optionalArticle.isPresent()) {
             Article article = optionalArticle.get();
             logger.info("Article found by slug {}: {}", slug, article);
             return article;
         } else {
             logger.error("Article not found by slug: {}", slug);
             throw new ArticleNotFoundException("Article not found");
         }
     }

    @Override
    public ArticleListResponseItemDto getArticleBySlug(String slug) {

        logger.info("Fetching article by slug: {}", slug);

        Article article = findArticleBySlug(slug);
        ArticleListResponseItemDto articleListResponseItemDto = modelMapper.map(article, ArticleListResponseItemDto.class);

        mapAuthorAndFavorited(article, articleListResponseItemDto);
        articleListResponseItemDto.setTagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()));

        logger.info("Article fetched successfully for slug: {}", slug);

        return articleListResponseItemDto;
    }

    @Override
    public ArticleListResponseItemDto createArticle(ArticleCreateRequestDto createRequestDto, UserEntity author) {

        logger.info("Creating article for author: {}", author.getUsername());

        Article article = modelMapper.map(createRequestDto, Article.class);
        article.setAuthor(author);

        Set<Tag> tags = createTags(createRequestDto.getTagList(), tagRepository);

        article.setTags(tags);
        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());
        article.setSlug(generateSlug(createRequestDto.getTitle()));

        Article savedArticle = articleRepository.save(article);

        ArticleListResponseItemDto articleDto = modelMapper.map(savedArticle, ArticleListResponseItemDto.class);

        articleDto.setTagList(tags.stream().map(Tag::getName).collect(Collectors.toList()));

        mapAuthorAndFavorited(article, articleDto);

        logger.info("Article created successfully for author: {}", author.getUsername());

        return articleDto;
    }

    @Override
    public ArticleListResponseItemDto updateArticle(String slug, UserEntity currentUser, UpdateArticleRequestDto updateArticleRequestDto) {
        Article article = findArticleBySlug(slug);
        validateArticleOwnership(currentUser, article.getAuthor());

        updateArticleFields(article, updateArticleRequestDto);
        article.setSlug(generateSlug(updateArticleRequestDto.getTitle()));
        article.setUpdatedAt(LocalDateTime.now());

        Article updatedArticle = articleRepository.save(article);
        ArticleListResponseItemDto responseItemDto = modelMapper.map(updatedArticle, ArticleListResponseItemDto.class);
        responseItemDto.setDescription(updatedArticle.getDescription());
        responseItemDto.setBody(updatedArticle.getBody());
        responseItemDto.setTagList(updatedArticle.getTags().stream().map(Tag::getName).collect(Collectors.toList()));

        mapAuthorAndFavorited(updatedArticle, responseItemDto);

        return responseItemDto;
    }


    @Override
    @Transactional
    public void deleteArticle(String slug, UserEntity currentUser) {
        Article article = findArticleBySlug(slug);
        validateArticleOwnership(currentUser, article.getAuthor());

        for (UserEntity user : article.getFavoritedBy()) {
            user.getFavoriteArticles().remove(article);
            userRepository.save(user);
        }

        commentRepository.deleteAll(article.getComments());

        articleRepository.delete(article);
    }


    @Override
    @Transactional
    public ArticleListResponseItemDto favoriteArticle(String slug, UserEntity user) {

            Article article = findArticleBySlug(slug);

        if (article.getFavoritedBy().contains(user)) {
            throw new ArticleAlreadyFavoritedException("Article already favorited by the user");
        }

        article.getFavoritedBy().add(user);
        article.setFavoritesCount(article.getFavoritesCount() + 1);

        Article savedArticle = articleRepository.save(article);

        user.getFavoriteArticles().add(savedArticle);
        userRepository.save(user);


        ArticleListResponseItemDto dto = modelMapper.map(savedArticle, ArticleListResponseItemDto.class);

        dto.setTagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        mapAuthorAndFavorited(article, dto);


        return dto;
    }

    @Override
    @Transactional
    public ArticleListResponseItemDto unfavoriteArticle(String slug, UserEntity currentUser) {

        Article article = findArticleBySlug(slug);

        if (!article.getFavoritedBy().contains(currentUser)) {
            throw new ArticleNotFoundException("Article is not favorited by the user");
        }

        article.getFavoritedBy().remove(currentUser);
        article.setFavoritesCount(article.getFavoritesCount() - 1);

        Article savedArticle = articleRepository.save(article);

        currentUser.getFavoriteArticles().remove(savedArticle);
        userRepository.save(currentUser);

        ArticleListResponseItemDto dto = modelMapper.map(savedArticle, ArticleListResponseItemDto.class);

        dto.setTagList(article.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
        mapAuthorAndFavorited(article, dto);
        return dto;

    }


    private void updateArticleFields(Article article, UpdateArticleRequestDto updateArticleRequestDto) {
        Optional.ofNullable(updateArticleRequestDto.getTitle()).ifPresent(article::setTitle);

        Optional.ofNullable(updateArticleRequestDto.getDescription()).ifPresent(article::setDescription);

        Optional.ofNullable(updateArticleRequestDto.getBody()).ifPresent(article::setBody);
    }


}


