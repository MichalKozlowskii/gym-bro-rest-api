package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User processOAuthPostLogin(OAuth2User oAuth2User) {
        String username = oAuth2User.getAttribute("login");

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            String randomPassword = generateRandomPassword();
            String hashedPassword = passwordEncoder.encode(randomPassword);

            user = userRepository.save(User.builder()
                    .username(username)
                    .password(hashedPassword)
                    .build());
        }

        return user;
    }

    @Override
    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16]; // 16 bytes = 128-bit security
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
