package com.api.medium_clone.controller;


import com.api.medium_clone.dto.*;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.service.ArticleService;
import com.api.medium_clone.service.CommentService;
import com.api.medium_clone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;

    private final CommentService commentService;


    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @GetMapping
    public ResponseEntity<ArticleListResponseDto> listArticles(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String favorited) {

        logger.info("Received request to list articles with parameters: limit={}, offset={}, tag={}, author={}, favorited={}", limit, offset, tag, author, favorited);

        ArticleListResponseDto articles = articleService.listArticles(limit, offset, tag, author, favorited);

        logger.info("Returning {} articles in response", articles.getArticles().size());

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/feed")
    public ResponseEntity<ArticleListResponseDto> getFeedArticles(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Received request to get feed articles with parameters: limit={}, offset={}, userDetails={}", limit, offset, userDetails);

        ArticleListResponseDto feedArticles = articleService.getFeedArticles(userDetails, limit, offset);

        logger.info("Returning {} feed articles in response", feedArticles.getArticles().size());

        return ResponseEntity.ok(feedArticles);
    }


    @GetMapping("/{slug}")
    public ResponseEntity<ArticleListResponseItemDto> getArticleBySlug(@PathVariable String slug) {
        logger.info("Received request to get article by slug: {}", slug);

        ArticleListResponseItemDto articleListResponseItemDto = articleService.getArticleBySlug(slug);

        logger.info("Feed articles : {} retrieved successfully" , articleListResponseItemDto);

        return ResponseEntity.ok(articleListResponseItemDto);
    }

    @PostMapping
    public ResponseEntity<ArticleListResponseItemDto> createArticle(
            @RequestBody @Valid ArticleCreateRequestDto createRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Creating article with request: {}", createRequestDto);

        UserEntity author = userService.getUserByUsername(userDetails.getUsername());
        ArticleListResponseItemDto responseDto = articleService.createArticle(createRequestDto, author);

        logger.info("Article created successfully");

        return ResponseEntity.ok(responseDto);
    }


    @PutMapping("/{slug}")
    public ResponseEntity<ArticleListResponseItemDto> updateArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails,
          @Valid @RequestBody UpdateArticleRequestDto updateArticleRequestDto) {

        logger.info("Updating article with slug: {}, Request: {}", slug, updateArticleRequestDto);

        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        ArticleListResponseItemDto updatedArticle = articleService.updateArticle(slug, currentUser, updateArticleRequestDto);

        logger.info("Article updated successfully");

        return ResponseEntity.ok(updatedArticle);
    }


    @DeleteMapping("/{slug}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails){

        logger.info("Deleting article with slug: {}", slug);

        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        articleService.deleteArticle(slug, currentUser);

        logger.info("Article deleted successfully");

        return ResponseEntity.ok("Article deleted successfully");
    }

    @PostMapping("/{slug}/comments")
    public ResponseEntity<CommentDto> addCommentToArticle(
            @PathVariable String slug,
            @RequestBody CommentRequestDto commentRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Adding comment to article with slug: {}", slug);

        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        Article article = articleService.findArticleBySlug(slug);
        CommentDto commentDto = commentService.addComment(article, currentUser, commentRequest.getBody());
        commentDto.setUsername(userDetails.getUsername());

        logger.info("Comment added successfully to article with slug: {}", slug);

        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @GetMapping("/{slug}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsForArticle(
            @PathVariable String slug ){

        logger.info("Fetching comments for article with slug: {}", slug);

        Article article = articleService.findArticleBySlug(slug);
        List<CommentDto> comments = commentService.getCommentsForArticle(article);

        logger.info("Comments fetched successfully for article with slug: {}", slug);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }


    @DeleteMapping("/{slug}/comments/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String slug,
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Deleting comment with ID {} for article with slug {}", id, slug);

        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        commentService.deleteComment(id , currentUser);

        logger.info("Comment with ID {} deleted successfully for article with slug {}", id, slug);

        return ResponseEntity.ok("Comment deleted successfully");
    }

    @PostMapping("/{slug}/favorite")
    public ResponseEntity<ArticleListResponseItemDto> favoriteArticle(@PathVariable String slug, @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Adding article with slug {} to favorites for user {}", slug, userDetails.getUsername());

        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        ArticleListResponseItemDto article = articleService.favoriteArticle(slug, currentUser);

        logger.info("Article with slug {} added to favorites for user {}", slug, userDetails.getUsername());

        return new ResponseEntity<>(article, HttpStatus.OK);
    }

    @DeleteMapping("/{slug}/favorite")
    public ResponseEntity<ArticleListResponseItemDto> unfavoriteArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Removing article with slug {} from favorites for user {}", slug, userDetails.getUsername());

        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        ArticleListResponseItemDto articleDto = articleService.unfavoriteArticle(slug, currentUser);

        logger.info("Article with slug {} removed from favorites for user {}", slug, userDetails.getUsername());

        return new ResponseEntity<>(articleDto, HttpStatus.OK);
    }

}

