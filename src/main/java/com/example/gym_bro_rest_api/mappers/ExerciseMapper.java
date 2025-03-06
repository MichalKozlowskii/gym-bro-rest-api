package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ExerciseMapper {
    ExerciseDTO exerciseToExerciseDto(Exercise exercise);
    Exercise exerciseDtoToExercise(ExerciseDTO exerciseDTO);
}
