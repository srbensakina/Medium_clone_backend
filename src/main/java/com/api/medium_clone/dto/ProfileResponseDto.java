package com.api.medium_clone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponseDto {
    private String username;
    private String bio;
    private String image;
    private boolean following;
}
