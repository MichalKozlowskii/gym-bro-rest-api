package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;

import java.util.Optional;

public interface ExerciseService {
    ExerciseDTO saveNewExercise(ExerciseDTO exerciseDTO, User user);
    Optional<ExerciseDTO> getExerciseById(Long id);
}
