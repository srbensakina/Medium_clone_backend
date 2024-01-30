package com.api.medium_clone.service;

import com.api.medium_clone.entity.UserEntity;

import java.util.Optional;


public interface UserService {
    Optional<UserEntity> getCurrentUser(String email);

    UserEntity updateUser(UserEntity userEntity);
}
