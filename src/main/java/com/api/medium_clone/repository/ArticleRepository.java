package com.api.medium_clone.repository;

import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findAll(Specification<Article> spec, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.author IN :authors")
    List<Article> findFeedArticlesByAuthors(List<UserEntity> authors, Pageable pageable);
}


