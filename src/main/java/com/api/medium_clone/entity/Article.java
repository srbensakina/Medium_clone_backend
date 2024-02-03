package com.api.medium_clone.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String slug;
    private String title;
    private String description;
    private String body;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean favorited = false;
    private int favoritesCount = 0;

    @ManyToOne()
    private UserEntity author;

    @OneToMany(mappedBy = "article")
    private List<Comment> comments;

}
