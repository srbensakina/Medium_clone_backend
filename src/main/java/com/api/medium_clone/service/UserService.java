package com.api.medium_clone.service;

import com.api.medium_clone.entity.UserEntity;


public interface UserService {
    UserEntity getCurrentUser(String email);

    UserEntity updateUser(UserEntity userEntity);
}
