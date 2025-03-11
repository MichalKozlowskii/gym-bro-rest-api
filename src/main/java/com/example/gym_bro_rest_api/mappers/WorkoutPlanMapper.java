package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = ExerciseMapper.class)
public interface WorkoutPlanMapper {
    @Mapping(source = "user.id", target = "userId")
    WorkoutPlanDTO workoutPlanToWorkoutPlanDto(WorkoutPlan workoutPlan);
}
