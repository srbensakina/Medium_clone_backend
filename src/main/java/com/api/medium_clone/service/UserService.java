package com.api.medium_clone.service;

import com.api.medium_clone.dto.UpdateUserRequestDto;
import com.api.medium_clone.dto.UserResponseDto;
import com.api.medium_clone.entity.UserEntity;


public interface UserService {
    UserResponseDto getCurrentUser(String username);
    UserEntity getUserByUsername(String username);

    UserResponseDto updateUser(String username , UpdateUserRequestDto updateUserRequestDto);
}
