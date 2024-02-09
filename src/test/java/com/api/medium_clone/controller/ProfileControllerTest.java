package com.api.medium_clone.controller;

import com.api.medium_clone.entity.UserEntity;
import com.api.medium_clone.security.*;
import com.api.medium_clone.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProfileController.class)
@Import(SecurityConfig.class)
@ExtendWith(SpringExtension.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JWTGenerator jwtUtil;

    @MockBean
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @MockBean
    private CustomerUserDetailsService detailsService;

    @InjectMocks
    private ProfileController profileController;

    @Autowired
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @WithMockUser("currentUser")
    void testUnfollowUser() throws Exception {
        String usernameToUnfollow = "someUser";
        String currentUser = "currentUser";

        UserEntity user = new UserEntity();
        user.setUsername(usernameToUnfollow);
        user.setBio("Some bio");
        user.setImage("Some image");

        when(profileService.getUserProfile(usernameToUnfollow)).thenReturn(user);

        doNothing().when(profileService).unfollowUser(usernameToUnfollow, currentUser);

        mockMvc.perform(delete("/api/profiles/" + usernameToUnfollow + "/follow")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(usernameToUnfollow))
                .andExpect(jsonPath("$.bio").value("Some bio"))
                .andExpect(jsonPath("$.image").value("Some image"))
                .andExpect(jsonPath("$.following").value(false));

        verify(profileService).getUserProfile(usernameToUnfollow);
        verify(profileService).unfollowUser(usernameToUnfollow, currentUser);
    }

}
