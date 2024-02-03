package com.api.medium_clone.controller;


import com.api.medium_clone.dto.*;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.Comment;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.service.ArticleService;
import com.api.medium_clone.service.ArticleServiceImpl;
import com.api.medium_clone.service.CommentService;
import com.api.medium_clone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleServiceImpl articleService;
    private final UserService userService;

    private final CommentService commentService;



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

        ArticleListResponseDto feedArticles = articleService.getFeedArticles(userDetails, limit, offset);
        return ResponseEntity.ok(feedArticles);
    }


    @GetMapping("/{slug}")
    public ResponseEntity<ArticleListResponseItemDto> getArticleBySlug(@PathVariable String slug) {
        ArticleListResponseItemDto articleListResponseItemDto = articleService.getArticleBySlug(slug);
        return ResponseEntity.ok(articleListResponseItemDto);
    }

    @PostMapping
    public ResponseEntity<ArticleListResponseItemDto> createArticle(
            @RequestBody @Valid ArticleCreateRequestDto createRequestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity author = userService.getUserByUsername(userDetails.getUsername());
        ArticleListResponseItemDto responseDto = articleService.createArticle(createRequestDto, author);
        return ResponseEntity.ok(responseDto);
    }


    @PutMapping("/{slug}")
    public ResponseEntity<ArticleListResponseItemDto> updateArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails,
          @Valid @RequestBody UpdateArticleRequestDto updateArticleRequestDto) {
        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        ArticleListResponseItemDto updatedArticle = articleService.updateArticle(slug, currentUser, updateArticleRequestDto);
        return ResponseEntity.ok(updatedArticle);
    }


    @DeleteMapping("/{slug}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails){
        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        articleService.deleteArticle(slug, currentUser);
        return ResponseEntity.ok("Article deleted successfully");
    }

    @PostMapping("/{slug}/comments")
    public ResponseEntity<CommentDto> addCommentToArticle(
            @PathVariable String slug,
            @RequestBody CommentRequestDto commentRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        Article article = articleService.findArticleBySlug(slug);
        CommentDto commentDto = commentService.addComment(article, currentUser, commentRequest.getBody());
        commentDto.setUsername(userDetails.getUsername());
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @GetMapping("/{slug}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsForArticle(
            @PathVariable String slug ){


        Article article = articleService.findArticleBySlug(slug);
        List<CommentDto> comments = commentService.getCommentsForArticle(article);

        return new ResponseEntity<>(comments, HttpStatus.OK);
    }


    @DeleteMapping("/{slug}/comments/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable String slug,
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        commentService.deleteComment(id , currentUser);

        return ResponseEntity.ok("Comment deleted successfully");
    }
}

