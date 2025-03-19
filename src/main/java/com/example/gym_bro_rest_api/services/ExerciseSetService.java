package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.model.ExerciseSetDTO;

public interface ExerciseSetService {
    ExerciseSetDTO saveNewExerciseSet(ExerciseSetDTO exerciseSetDTO);
}
