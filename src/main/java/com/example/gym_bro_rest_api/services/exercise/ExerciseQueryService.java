package com.example.gym_bro_rest_api.services.exercise;

import com.example.gym_bro_rest_api.entities.Exercise;

public interface ExerciseQueryService {
    Exercise getExerciseById(Long id);
}
