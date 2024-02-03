package com.api.medium_clone.repository;

import com.api.medium_clone.dto.CommentDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByArticleOrderByCreatedAt(Article article);
}
