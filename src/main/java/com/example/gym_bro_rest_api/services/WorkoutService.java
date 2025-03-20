package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;

public interface WorkoutService {
    Long saveNewWorkout(WorkoutCreationDTO workoutCreationDTO, User user);
}
