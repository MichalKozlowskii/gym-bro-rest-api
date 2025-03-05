package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.model.UserDTO;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import com.example.gym_bro_rest_api.services.JwtService;
import com.example.gym_bro_rest_api.services.OAuth2UserService;
import com.example.gym_bro_rest_api.services.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import javax.naming.AuthenticationException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@Transactional
@Slf4j
class AuthControllerIT {
    @Autowired
    AuthController authController;

    @Autowired
    UserService userService;

    UserDTO existingUser;

    @BeforeEach
    void setup() {
        existingUser = userService.saveNewUser(UserDTO.builder()
                .username("test")
                .password("password")
                .build());
    }

    @Test
    void testLogin_UserExists_RightPassword() {
        UserDTO loginRequest = UserDTO.builder()
                .username("test")
                .password("password")
                .build();

        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(response.getBody()).containsKey("jwt_token");
        assertThat(response.getBody().get("jwt_token")).isNotEmpty();
    }

    @Test
    void testLogin_UserExists_WrongPassword() {
        UserDTO loginRequest = UserDTO.builder()
                .username("test")
                .password("wrongPassword")
                .build();

        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(401));
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Invalid username or password."));
    }

    @Test
    void testLogin_UserDontExist() {
        UserDTO loginRequest = UserDTO.builder()
                .username("wrongName")
                .password("password")
                .build();

        ResponseEntity<Map<String, String>> response = authController.login(loginRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(401));
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Invalid username or password."));
    }
}