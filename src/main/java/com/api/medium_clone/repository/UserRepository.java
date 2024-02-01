package com.api.medium_clone.repository;

import com.api.medium_clone.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity , Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsernameOrEmail(String username , String email);



}
