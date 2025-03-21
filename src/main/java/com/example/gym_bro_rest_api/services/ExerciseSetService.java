package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;

public interface ExerciseSetService {
    ExerciseSet saveNewExerciseSet(ExerciseSetDTO exerciseSetDTO, Workout workout);
}


