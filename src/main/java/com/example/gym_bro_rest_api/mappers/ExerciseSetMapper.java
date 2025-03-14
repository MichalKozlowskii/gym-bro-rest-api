package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = ExerciseMapper.class)
public interface ExerciseSetMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "workout.id", target = "workoutId")
    ExerciseSetDTO exerciseSetToExerciseSetDTO(ExerciseSet exerciseSetDTO);
}
