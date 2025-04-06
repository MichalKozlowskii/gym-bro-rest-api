package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.StatsView;
import com.example.gym_bro_rest_api.services.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/api/stats/{exerciseId}")
    ResponseEntity<StatsView> getExerciseStatsById(@PathVariable("exerciseId") Long exerciseId,
                                                   @AuthenticationPrincipal User user) {
        return statsService.getExerciseStats(exerciseId, user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}
