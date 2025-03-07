package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ExerciseMapper {
    @Mapping(source = "user.id", target = "userId")
    ExerciseDTO exerciseToExerciseDto(Exercise exercise);
}
