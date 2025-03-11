package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface ExerciseService {
    ExerciseDTO saveNewExercise(ExerciseDTO exerciseDTO, User user);
    Optional<ExerciseDTO> getExerciseById(Long id);
    Optional<ExerciseDTO> updateExerciseById(Long id, ExerciseDTO exerciseDTO, User user);
    void deleteExerciseById(Long id, User user);
    Page<ExerciseDTO> listExercisesOfUser(User user, Integer pageNumber, Integer pageSize);
}
