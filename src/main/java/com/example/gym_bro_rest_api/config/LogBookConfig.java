package com.example.gym_bro_rest_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zalando.logbook.BodyFilter;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.json.JsonBodyFilters;

import java.util.Set;

@Configuration
public class LogBookConfig {
    @Bean
    public Logbook logbook() {
        BodyFilter jwtFilter = JsonBodyFilters.replaceJsonStringProperty(Set.of("jwt_token"), "XXX");

        return Logbook.builder()
                .bodyFilter(jwtFilter)
                .build();
    }
}
