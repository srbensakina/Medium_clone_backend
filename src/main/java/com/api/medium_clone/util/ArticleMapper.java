package com.api.medium_clone.util;

import com.api.medium_clone.dto.ArticleListResponseItemDto;
import com.api.medium_clone.entity.Article;
import com.api.medium_clone.entity.Tag;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.exception.ArticleAccessDeniedException;
import com.api.medium_clone.repository.TagRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ArticleMapper {

    public static void validateArticleOwnership(UserEntity currentUser, UserEntity articleAuthor) {
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


    public static Set<Tag> createTags(List<String> tags, TagRepository tagRepository){
        return   tags.stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        }))
                .collect(Collectors.toSet());
    }




    public static void mapAuthorAndFavorited(Article article, ArticleListResponseItemDto dto) {
        dto.setAuthor(article.getAuthor().getUsername());
        dto.setFavorited(!article.getFavoritedBy().isEmpty());
    }

}
