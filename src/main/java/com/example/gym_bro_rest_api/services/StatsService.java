package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.StatsView;

import java.util.Optional;

public interface StatsService {
    Optional<StatsView> getExerciseStats(Long exerciseId, User user);
}
