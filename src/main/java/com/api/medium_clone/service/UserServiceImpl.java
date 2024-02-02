package com.api.medium_clone.service;

import com.api.medium_clone.dto.UpdateUserRequestDto;
import com.api.medium_clone.dto.UserResponseDto;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.exception.UserNotFoundException;
import com.api.medium_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserResponseDto getCurrentUser(String username) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity userEntity = getUserByUsername(username);

        UserResponseDto userResponseDto =    modelMapper.map(userEntity, UserResponseDto.class);
        userResponseDto.setToken(extractJwtToken(authentication));
        return userResponseDto;
    }

    @Override
    public UserEntity getUserByUsername(String username) {
      return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    public UserResponseDto updateUser(String username, UpdateUserRequestDto updateUserRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        updateUserFields(userEntity, updateUserRequestDto);
        UserEntity updatedUser = userRepository.save(userEntity);

                UserResponseDto userResponseDto =    modelMapper.map(updatedUser, UserResponseDto.class);
                userResponseDto.setToken(extractJwtToken(authentication));
                return userResponseDto;
    }



    private void updateUserFields(UserEntity user, UpdateUserRequestDto updateUserRequestDto) {
        Optional.ofNullable(updateUserRequestDto.getEmail())
                .ifPresent(user::setEmail);

        Optional.ofNullable(updateUserRequestDto.getBio())
                .ifPresent(user::setBio);

        Optional.ofNullable(updateUserRequestDto.getImage())
                .ifPresent(user::setImage);
    }


    private String extractJwtToken(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        } else {
            return null;
        }

    }
}
