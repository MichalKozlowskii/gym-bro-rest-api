package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.UserDTO;
import com.example.gym_bro_rest_api.services.JwtService;
import com.example.gym_bro_rest_api.services.OAuth2UserService;
import com.example.gym_bro_rest_api.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final OAuth2UserService oAuth2UserService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDTO userDTO) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    userDTO.getUsername(), userDTO.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password."));
        }

        String token = jwtService.generateToken(userDTO.getUsername());
        return ResponseEntity.ok(Map.of("jwt_token", token));
    }

    /*@PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserDTO userDTO) {

    }*/

    @GetMapping("/oauth-success")
    public ResponseEntity<Map<String, String>> oAuthSuccess(@RequestParam("token") String token) {
        oAuth2UserService.processOAuthPostLogin(token);

        return ResponseEntity.ok(Map.of("jwt_token", token));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testAuth(@AuthenticationPrincipal User user) {
        String username = user.getUsername();

        // Return a response with the authenticated user's username
        return ResponseEntity.ok("Authenticated as: " + username);
    }
}
