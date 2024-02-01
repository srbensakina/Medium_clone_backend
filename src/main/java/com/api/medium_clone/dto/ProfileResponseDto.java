package com.api.medium_clone.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDto {
    private String username;
    private String bio;
    private String image;
    private boolean following;
}
