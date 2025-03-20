package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.NoAccessException;
import com.example.gym_bro_rest_api.controller.NotFoundException;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;
import com.example.gym_bro_rest_api.repositories.WorkoutRepository;
import com.example.gym_bro_rest_api.services.workoutplan.WorkoutPlanQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutPlanQueryService workoutPlanQueryService;
    private final WorkoutRepository workoutRepository;
    @Override
    public Long saveNewWorkout(WorkoutCreationDTO workoutCreationDTO, User user) {
        WorkoutPlan workoutPlan = workoutPlanQueryService.getWorkoutPlanById(workoutCreationDTO.getWorkoutPlanId());

        if (!workoutPlan.getUser().equals(user)) {
            throw new NoAccessException();
        }

        Workout workout = workoutRepository.save(Workout.builder()
                .workoutPlan(workoutPlan)
                .user(user)
                .build());

        return workout.getId();
    }
}
