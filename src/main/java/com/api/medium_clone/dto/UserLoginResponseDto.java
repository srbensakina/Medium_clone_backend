package com.api.medium_clone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserLoginResponseDto {

    private boolean success;
    private String message;
    private String token;

}
