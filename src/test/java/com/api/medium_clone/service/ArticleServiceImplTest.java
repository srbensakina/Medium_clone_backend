package com.api.medium_clone.service;

import com.api.medium_clone.dto.ArticleListResponseDto;
import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.dto.UpdateArticleRequestDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.Comment;
import com.api.medium_clone.entity.Tag;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.exception.ArticleNotFoundException;
import com.api.medium_clone.repository.ArticleRepository;
import com.api.medium_clone.repository.CommentRepository;
import com.api.medium_clone.repository.UserRepository;
import org.hibernate.mapping.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetArticleBySlug() {
        String slug = "test-slug";
        Article article = new Article();
        article.setTitle("Test Article");
        article.setBody("Test body");
        article.setAuthor(new UserEntity());
        article.setTags(new HashSet<>());
        ArticleListResponseItemDto expectedDto = new ArticleListResponseItemDto();
        expectedDto.setTitle("Test Article");
        expectedDto.setBody("Test body");
        expectedDto.setAuthor("Test Author");
        expectedDto.setTagList(new ArrayList<>());

        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(article));

        when(modelMapper.map(article, ArticleListResponseItemDto.class)).thenReturn(expectedDto);

        ArticleListResponseItemDto resultDto = articleService.getArticleBySlug(slug);

        verify(articleRepository, times(1)).findBySlug(slug);

        verify(modelMapper, times(1)).map(article, ArticleListResponseItemDto.class);

        assertNotNull(resultDto);
        assertEquals(expectedDto.getTitle(), resultDto.getTitle());
        assertEquals(expectedDto.getBody(), resultDto.getBody());
        assertEquals(expectedDto.getAuthor(), resultDto.getAuthor());
        assertEquals(expectedDto.getTagList(), resultDto.getTagList());
    }

    @Test
    void testGetArticleBySlug_ThrowsArticleNotFoundException() {
        String slug = "non-existent-slug";

        when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

        assertThrows(ArticleNotFoundException.class, () -> articleService.getArticleBySlug(slug));

        verify(articleRepository, times(1)).findBySlug(slug);

        verify(modelMapper, never()).map(any(), any());
    }


    @Test
    void testListArticles() {
        int limit = 10;
        int offset = 0;
        String tag = "test-tag";
        String author = "test-author";
        String favorited = "test-favorited";

        UserEntity user = new UserEntity();
        user.setUsername(author);

        List<Article> articles = new ArrayList<>();
        Article article = new Article();
        article.setAuthor(user);
        articles.add(article);
        PageImpl<Article> page = new PageImpl<>(articles);
        when(articleRepository.findAll(any(Specification.class), any())).thenReturn(page);

        ArticleListResponseItemDto dto = new ArticleListResponseItemDto();
        when(modelMapper.map(any(Article.class), eq(ArticleListResponseItemDto.class))).thenReturn(dto);

        ArticleListResponseDto responseDto = articleService.listArticles(limit, offset, tag, author, favorited);

        verify(articleRepository, times(1)).findAll(any(Specification.class), any());

        verify(modelMapper, times(1)).map(any(Article.class), eq(ArticleListResponseItemDto.class));

        assertEquals(articles.size(), responseDto.getArticles().size());
        assertEquals(articles.size(), responseDto.getArticlesCount());
    }


    @Test
    void testUnfavoriteArticle() {
        // Given
        String slug = "test-article";
        UserEntity currentUser = new UserEntity();
        currentUser.setId(1L);

        UserEntity author = new UserEntity();
        author.setUsername("test-author");

        Article article = new Article();
        article.setSlug(slug);
        article.setFavoritesCount(1);
        article.setAuthor(author);


        HashSet<UserEntity> favoritedBy = new HashSet<>();
        favoritedBy.add(currentUser);
        article.setFavoritedBy(favoritedBy);

        Article savedArticle = new Article();
        savedArticle.setSlug(slug);
        savedArticle.setFavoritesCount(0);

        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(article));
        when(articleRepository.save(article)).thenReturn(savedArticle);

        when(modelMapper.map(savedArticle, ArticleListResponseItemDto.class)).thenReturn(new ArticleListResponseItemDto());

        ArticleListResponseItemDto dto = articleService.unfavoriteArticle(slug, currentUser);

        assertEquals(savedArticle.getFavoritesCount(), 0);
        assertEquals(dto.getFavoritesCount(), 0);

        verify(articleRepository, times(1)).findBySlug(slug);
        verify(articleRepository, times(1)).save(article);
        verify(modelMapper, times(1)).map(savedArticle, ArticleListResponseItemDto.class);
    }


    @Test
    void testUnfavoriteArticleArticleNotFound() {
        String slug = "non-existent-article";
        UserEntity currentUser = new UserEntity();

        when(articleRepository.findBySlug(slug)).thenReturn(Optional.empty());

        assertThrows(ArticleNotFoundException.class, () -> articleService.unfavoriteArticle(slug, currentUser));

        verify(articleRepository, times(1)).findBySlug(slug);
        verify(articleRepository, never()).save(any());
        verify(modelMapper, never()).map(any(), any());
    }


    @Test
    void testFavoriteArticle() {
        String slug = "test-article";

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("test-author");

        Article article = new Article();
        article.setSlug(slug);
        article.setFavoritesCount(0);
        article.setAuthor(user);

        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(article));

        when(articleRepository.save(any(Article.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(userRepository.save(user)).thenReturn(user);

        when(modelMapper.map(article, ArticleListResponseItemDto.class))
                .thenAnswer(invocation -> {
                    Article source = invocation.getArgument(0);
                    ArticleListResponseItemDto dto = new ArticleListResponseItemDto();
                    dto.setFavoritesCount(source.getFavoritesCount());
                    return dto;
                });

        ArticleListResponseItemDto dto = articleService.favoriteArticle(slug, user);

        assertEquals(article.getFavoritesCount(), 1);
        assertEquals(dto.getFavoritesCount(), 1);

        verify(articleRepository, times(1)).findBySlug(slug);
        verify(articleRepository, times(1)).save(article);
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(article, ArticleListResponseItemDto.class);
    }

    @Test
    void testDeleteArticle() {
        // Set up test data
        String slug = "test-article";
        UserEntity currentUser = new UserEntity();
        currentUser.setId(1L);
        currentUser.setUsername("test-user");

        Article article = new Article();
        article.setSlug(slug);
        article.setAuthor(currentUser);

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setArticle(article);
        // Set up more comments if needed...

        UserEntity favoritedUser = new UserEntity();
        favoritedUser.setId(2L);
        favoritedUser.setUsername("favorited-user");
        article.getFavoritedBy().add(favoritedUser);
        favoritedUser.getFavoriteArticles().add(article);

        // Mock the dependencies
        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(article));

        // Call the method
        articleService.deleteArticle(slug, currentUser);

        // Verify the deletions
        verify(articleRepository, times(1)).delete(article);
        verify(commentRepository, times(1)).deleteAll(article.getComments());
        verify(userRepository, times(1)).save(favoritedUser);
    }


    @Test
    void testUpdateArticle() {
        String slug = "test-article";
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";
        String newBody = "Updated Body";
        UpdateArticleRequestDto updateArticleRequestDto = new UpdateArticleRequestDto();
        updateArticleRequestDto.setTitle(newTitle);
        updateArticleRequestDto.setDescription(newDescription);
        updateArticleRequestDto.setBody(newBody);

        UserEntity currentUser = new UserEntity();
        currentUser.setId(1L);
        currentUser.setUsername("test-user");

        Article article = new Article();
        article.setSlug(slug);
        article.setTitle("Original Title");
        article.setDescription("Original Description");
        article.setBody("Original Body");
        article.setUpdatedAt(LocalDateTime.now());
        article.setTags(new HashSet<>());
        article.setFavoritedBy(new HashSet<>());
        article.setAuthor(currentUser);

        Article updatedArticle = new Article();
        updatedArticle.setSlug(slug);
        updatedArticle.setTitle(newTitle);
        updatedArticle.setDescription(newDescription);
        updatedArticle.setBody(newBody);
        updatedArticle.setUpdatedAt(LocalDateTime.now());
        updatedArticle.setTags(new HashSet<>());
        updatedArticle.setFavoritedBy(new HashSet<>());
        updatedArticle.setAuthor(currentUser);

        ArticleListResponseItemDto responseItemDto = new ArticleListResponseItemDto();
        responseItemDto.setTitle(newTitle);
        responseItemDto.setDescription(newDescription);
        responseItemDto.setBody(newBody);
        responseItemDto.setTagList(Collections.singletonList("Tag2"));

        when(articleRepository.findBySlug(slug)).thenReturn(Optional.of(article));
        when(articleRepository.save(article)).thenReturn(updatedArticle);
        when(modelMapper.map(updatedArticle, ArticleListResponseItemDto.class)).thenReturn(responseItemDto);

        ArticleListResponseItemDto dto = articleService.updateArticle(slug, currentUser, updateArticleRequestDto);

        assertEquals(responseItemDto.getTitle(), dto.getTitle());
        assertEquals(responseItemDto.getDescription(), dto.getDescription());
        assertEquals(responseItemDto.getBody(), dto.getBody());
        assertEquals(responseItemDto.getTagList(), dto.getTagList());

        verify(articleRepository, times(1)).findBySlug(slug);
        verify(articleRepository, times(1)).save(article);
        verify(modelMapper, times(1)).map(updatedArticle, ArticleListResponseItemDto.class);
    }



}