package com.api.medium_clone.controller;

import com.api.medium_clone.dto.UpdateUserRequestDto;
import com.api.medium_clone.dto.UserResponseDto;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;
    private final ModelMapper modelMapper;



    @GetMapping
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = userDetails.getUsername();
        System.out.println("email " + userEmail);
        UserEntity currentUser = userService.getCurrentUser(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        UserResponseDto userResponseDto = modelMapper.map(currentUser, UserResponseDto.class);
         userResponseDto.setToken(extractJwtToken(authentication));
        return ResponseEntity.ok(userResponseDto);
    }


    private String extractJwtToken(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        } else {
            return null;
        }


    }

    @PutMapping
    public ResponseEntity<?> updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = userDetails.getUsername();

            UserEntity currentUser = userService.getCurrentUser(userEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            userService.updateUserFields(currentUser, updateUserRequestDto);

            UserEntity updatedUser = userService.updateUser(currentUser);

            UserResponseDto userResponseDto = modelMapper.map(updatedUser, UserResponseDto.class);
            userResponseDto.setToken(extractJwtToken(authentication));

            return ResponseEntity.ok(userResponseDto);
        } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
    }



}
