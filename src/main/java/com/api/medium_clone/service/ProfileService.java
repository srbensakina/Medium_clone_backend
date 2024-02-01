package com.api.medium_clone.service;

import com.api.medium_clone.entity.UserEntity;

import java.util.Optional;


public interface ProfileService {
    Optional<UserEntity> getUserProfile(String username);

    void followUser(String username, String followerUsername);

    boolean isFollowing(UserEntity follower, UserEntity followedUser) ;

     void unfollowUser(String unfollowedUsername, String followerEmail) ;



    }
