package com.api.medium_clone.service;

import com.api.medium_clone.dto.UpdateUserRequestDto;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<UserEntity> getCurrentUser (String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity updateUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public void updateUserFields(UserEntity user, UpdateUserRequestDto updateUserRequestDto) {
        Optional.ofNullable(updateUserRequestDto.getEmail())
                .ifPresent(user::setEmail);

        Optional.ofNullable(updateUserRequestDto.getBio())
                .ifPresent(user::setBio);

        Optional.ofNullable(updateUserRequestDto.getImage())
                .ifPresent(user::setImage);
    }


}