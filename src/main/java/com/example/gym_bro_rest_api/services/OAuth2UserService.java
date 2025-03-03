package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserService {
    User processOAuthPostLogin(OAuth2User oAuth2User);
    String generateRandomPassword();
}
