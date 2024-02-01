package com.api.medium_clone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    @Email
    private String email;
    private String bio;
    private String image;

    @OneToMany(mappedBy = "follower")
    private List<Follow> following;

    @OneToMany(mappedBy = "followed")
    private List<Follow> followers;

    @OneToMany(mappedBy = "author")
    private List<Article> articleList;


    @OneToMany(mappedBy = "user")
    private List<Comment> commentList;

}
