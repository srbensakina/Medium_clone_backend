package com.api.medium_clone.repository;

import com.api.medium_clone.entity.Follow;
import com.api.medium_clone.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowedAndFollower(UserEntity follower, UserEntity followedUser);

    Optional<Follow> findByFollowedAndFollower(UserEntity follower, UserEntity followedUser);

    List<Follow> findByFollower(UserEntity follower);

}
