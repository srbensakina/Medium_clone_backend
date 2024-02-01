package com.api.medium_clone.specifications;

import com.api.medium_clone.entity.Article;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ArticleSpecifications {

    public static Specification<Article> hasTag(String tag) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isMember(tag, root.get("tagList"));
    }

    public static Specification<Article> hasAuthor(String authorUsername) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("author").get("username"), authorUsername);
    }

    public static Specification<Article> isFavoritedBy(String username) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue(root.get("favoritedBy").get("username").in(username));
    }

    public static Specification<Article> orderByMostRecent() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaBuilder.conjunction();
        };
    }

}
