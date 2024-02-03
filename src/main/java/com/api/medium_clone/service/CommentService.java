package com.api.medium_clone.service;

import com.api.medium_clone.dto.*;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.Comment;
import com.api.medium_clone.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Article article, UserEntity user, String body) ;

    List<CommentDto> getCommentsForArticle(Article article);

    void deleteComment(Long commentId, UserEntity currentUser);

}
