package com.api.medium_clone.controller;

import com.api.medium_clone.dto.ProfileResponseDto;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.service.ProfileService;
import com.api.medium_clone.service.ProfileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{username}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable String username,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = profileService.getUserProfile(username);

        boolean isCurrentUserFollowing = false;

        if (userDetails != null) {
            UserEntity currentUser = profileService.getUserProfile(userDetails.getUsername());

            isCurrentUserFollowing = profileService.isFollowing(currentUser, user);
        }

        ProfileResponseDto profileResponseDto = ProfileResponseDto.builder()
                .bio(user.getBio())
                .username(user.getUsername())
                .image(user.getImage())
                .following(isCurrentUserFollowing)
                .build();

        return ResponseEntity.ok(profileResponseDto);
    }

    @PostMapping("/{username}/follow")
    public ResponseEntity<ProfileResponseDto> followUser(@PathVariable String username,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        profileService.followUser(username, userDetails.getUsername());

        return getProfile(username, userDetails);
    }


    @DeleteMapping("/{username}/follow")
    public ResponseEntity<ProfileResponseDto> unfollowUser(@PathVariable String username,
                                                           @AuthenticationPrincipal UserDetails userDetails) {
        profileService.unfollowUser(username, userDetails.getUsername());

        return getProfile(username, userDetails);
    }


}






