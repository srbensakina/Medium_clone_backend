package com.api.medium_clone.controller;


import com.api.medium_clone.dto.ArticleCreateRequestDto;
import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.dto.UpdateArticleRequestDto;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.service.ArticleService;
import com.api.medium_clone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;



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
    public ResponseEntity<Void> deleteArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal UserDetails userDetails){
        UserEntity currentUser = userService.getUserByUsername(userDetails.getUsername());
        articleService.deleteArticle(slug, currentUser);
        return ResponseEntity.noContent().build();
    }


}

