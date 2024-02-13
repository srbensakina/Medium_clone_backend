package com.api.medium_clone.controller;

import com.api.medium_clone.dto.UserAuthDto;
import com.api.medium_clone.dto.UserLoginResponseDto;
import com.api.medium_clone.dto.UserRegisterDto;
import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.repository.UserRepository;
import com.api.medium_clone.service.security.JWTGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    private final JWTGenerator jwtGenerator;

    @PostMapping()
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
          if (userRepository.existsByUsernameOrEmail(userRegisterDto.getUsername() , userRegisterDto.getEmail())) {
        return new ResponseEntity<>("Username or email is taken !! ", HttpStatus.BAD_REQUEST);
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userRegisterDto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(userRegisterDto.getPassword()));
        userEntity.setEmail(userRegisterDto.getEmail());


        userRepository.save(userEntity);
        return new ResponseEntity<>("User Register successfully !! ", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login( @Valid @RequestBody UserAuthDto userAuthDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userAuthDto.getUsername(), userAuthDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);
        UserLoginResponseDto responseDto = new UserLoginResponseDto();
        responseDto.setSuccess(true);
        responseDto.setMessage("login successful !!");
        responseDto.setToken(token);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}