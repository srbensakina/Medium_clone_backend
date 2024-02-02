package com.api.medium_clone.controller;

import com.api.medium_clone.dto.UpdateUserRequestDto;
import com.api.medium_clone.dto.UserResponseDto;
import com.api.medium_clone.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

        @GetMapping
        public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
            String username = userDetails.getUsername();
            UserResponseDto userResponseDto = userService.getCurrentUser(username);
            return ResponseEntity.ok(userResponseDto);
        }

        @PutMapping
        public ResponseEntity<UserResponseDto> updateUser(
                @AuthenticationPrincipal UserDetails userDetails,
                @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
            String username = userDetails.getUsername();
            UserResponseDto updatedUser = userService.updateUser(username, updateUserRequestDto);
            return ResponseEntity.ok(updatedUser);
        }
    }








