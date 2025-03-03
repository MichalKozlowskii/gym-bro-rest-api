package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.UserDTO;
import com.example.gym_bro_rest_api.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDTO userDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(),
                userDTO.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(userDTO.getUsername());
        String token = jwtService.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testAuth(@AuthenticationPrincipal User user) {
        String username = user.getUsername();

        // Return a response with the authenticated user's username
        return ResponseEntity.ok("Authenticated as: " + username);
    }
}
