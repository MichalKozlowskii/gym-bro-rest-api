package com.example.gym_bro_rest_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Applies to all your REST endpoints
                .allowedOrigins("*")  // Change this to specific origin(s) in production
                .allowedMethods("*")
                .allowedHeaders("*");

        // Optional: More precise control for Springdoc endpoints
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("*")
                .allowedMethods("*");

        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("*")
                .allowedMethods("*");

        registry.addMapping("/swagger-ui.html")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}

