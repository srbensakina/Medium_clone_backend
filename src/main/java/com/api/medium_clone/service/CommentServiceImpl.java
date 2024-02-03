package com.api.medium_clone.service;

import com.api.medium_clone.dto.CommentDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.Comment;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.exception.ArticleAccessDeniedException;
import com.api.medium_clone.exception.CommentNotFoundException;
import com.api.medium_clone.exception.UserNotFoundException;
import com.api.medium_clone.repository.ArticleRepository;
import com.api.medium_clone.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    private final ModelMapper modelMapper;

    @Override
    public CommentDto addComment(Article article, UserEntity user, String body) {

        Comment comment = Comment.builder().article(article).user(user).body(body).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

        Comment savedComment = commentRepository.save(comment);

        article.getComments().add(savedComment);
        articleRepository.save(article);

        return modelMapper.map(savedComment , CommentDto.class);
    }

    private void validateCommentOwnership(UserEntity currentUser, UserEntity commentOwner) {
        if (!currentUser.equals(commentOwner)) {
            throw new ArticleAccessDeniedException("You don't have permission to delete this comment");
        }

    }

    @Override
    public List<CommentDto> getCommentsForArticle(Article article) {

        return commentRepository.findByArticleOrderByCreatedAt(article).stream()
                .map(comment -> {
                    CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
                    commentDto.setUsername(comment.getUser().getUsername());
                    return commentDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId, UserEntity currentUser) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new CommentNotFoundException("Comment not found"));
        assert false;

        validateCommentOwnership(currentUser, comment.getUser());

        commentRepository.deleteById(commentId);
    }
}
