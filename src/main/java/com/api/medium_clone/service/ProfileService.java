package com.api.medium_clone.service;

import com.api.medium_clone.dto.ProfileResponseDto;
import com.api.medium_clone.dto.UserResponseDto;
import com.api.medium_clone.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;


public interface ProfileService {
    UserEntity getUserProfile(String username);

    void followUser(String username, String followerUsername);

    boolean isFollowing(UserEntity follower, UserEntity followedUser) ;

     void unfollowUser(String unfollowedUsername, String followerEmail) ;



    }
