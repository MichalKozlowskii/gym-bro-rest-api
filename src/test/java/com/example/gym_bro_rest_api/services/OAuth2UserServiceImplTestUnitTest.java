package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OAuth2UserServiceImplTestUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private OAuth2UserServiceImpl oAuth2UserService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessOAuthPostLoginNewUser() {
        String githubUsername = "newUser";
        String encodedPassword = "hashedPassword123";

        when(jwtService.extractUsername(anyString())).thenReturn(githubUsername);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = oAuth2UserService.processOAuthPostLogin("321412421sd");

        assertNotNull(createdUser);
        assertEquals(githubUsername, createdUser.getUsername());
        assertEquals(encodedPassword, createdUser.getPassword());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testProcessOAuthPostLoginExistingUser() {
        String githubUsername = "existingUser";
        User existingUser = User.builder().username(githubUsername).password("existingPassword").build();

        when(jwtService.extractUsername(anyString())).thenReturn(githubUsername);
        when(userRepository.findByUsername(githubUsername)).thenReturn(Optional.of(existingUser));

        User returnedUser = oAuth2UserService.processOAuthPostLogin("1342141241");

        assertNotNull(returnedUser);
        assertEquals(githubUsername, returnedUser.getUsername());
        assertEquals("existingPassword", returnedUser.getPassword());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGenerateRandomPassword() {
        String password1 = oAuth2UserService.generateRandomPassword();
        String password2 = oAuth2UserService.generateRandomPassword();

        assertNotNull(password1);
        assertNotNull(password2);
        assertNotEquals(password1, password2);
    }
}