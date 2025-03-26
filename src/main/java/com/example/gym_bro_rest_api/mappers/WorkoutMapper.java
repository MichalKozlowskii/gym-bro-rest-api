package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.model.workout.WorkoutViewDTO;

public interface WorkoutMapper {
    WorkoutViewDTO workoutToWorkoutViewDTO(Workout workout);
}
