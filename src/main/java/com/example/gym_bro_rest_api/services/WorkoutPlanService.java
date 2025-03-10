package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;

import java.util.Optional;

public interface WorkoutPlanService {
    WorkoutPlanDTO saveNewWorkoutPlan(WorkoutPlanDTO workoutPlanDTO, User user);
    Optional<WorkoutPlanDTO> getWorkoutPlanById(Long id);

    Optional<Object> updateWorkoutPlanById(Long id, WorkoutPlanDTO workoutPlanDTO, User user);
}
