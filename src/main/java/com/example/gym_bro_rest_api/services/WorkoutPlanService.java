package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface WorkoutPlanService {
    WorkoutPlanDTO saveNewWorkoutPlan(WorkoutPlanDTO workoutPlanDTO, User user);
    WorkoutPlanDTO getWorkoutPlanById(Long id, User user);

    void updateWorkoutPlanById(Long id, WorkoutPlanDTO workoutPlanDTO, User user);

    void deleteWorkoutPlanById(Long id, User user);

    Page<WorkoutPlanDTO> listExercisesOfUser(User user, Integer pageNumber, Integer pageSize);
}
