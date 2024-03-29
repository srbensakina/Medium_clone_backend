package com.api.medium_clone.service;

import com.api.medium_clone.entity.Follow;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.exception.UserNotFoundException;
import com.api.medium_clone.repository.FollowRepository;
import com.api.medium_clone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;



    @Override
    public UserEntity getUserProfile(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No user with this Username"));

    }

    @Override
    public void followUser(String followedUsername , String followerUsername) {
        UserEntity followedUser = userRepository.findByUsername(followedUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + followedUsername));

        UserEntity follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + followedUsername));

        if (!isFollowing(follower, followedUser)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowed(followedUser);
            followRepository.save(follow);
        }
    }


    public void unfollowUser(String unfollowedUsername, String followerEmail) {
        UserEntity unfollowedUser = userRepository.findByUsername(unfollowedUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + unfollowedUsername));

        UserEntity follower = userRepository.findByUsername(followerEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + followerEmail));

        Follow follow = followRepository.findByFollowedAndFollower(unfollowedUser, follower)
                .orElseThrow(() -> new UserNotFoundException("User is not being followed"));

        followRepository.delete(follow);
    }


    @Override
    public boolean isFollowing(UserEntity follower, UserEntity followedUser) {
        return followRepository.existsByFollowedAndFollower(followedUser, follower);
    }

    public List<UserEntity> getFollowedUsers(UserEntity follower) {
        List<Follow> follows = followRepository.findByFollower(follower);
        return follows.stream()
                .map(Follow::getFollowed)
                .collect(Collectors.toList());
    }
}
