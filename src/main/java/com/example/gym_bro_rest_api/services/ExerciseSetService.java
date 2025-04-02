package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import org.apache.coyote.BadRequestException;

import java.util.Optional;

public interface ExerciseSetService {
    ExerciseSet saveNewExerciseSet(ExerciseSetDTO exerciseSetDTO, Workout workout) throws BadRequestException;
    Optional<ExerciseSet> getExerciseSetById(Long id);
    Boolean deleteSetById(Long id);
}


