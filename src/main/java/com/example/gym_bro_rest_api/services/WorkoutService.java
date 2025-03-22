package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;

public interface WorkoutService {
    Long saveNewWorkout(WorkoutCreationDTO workoutCreationDTO, User user);
    Workout addNewSet(Long workoutId, ExerciseSetDTO exerciseSetDTO, User user);
    Boolean deleteSet(Long workoutId, Long setId, User user);
    void deleteWorkoutById(Long workoutId, User user);
}
