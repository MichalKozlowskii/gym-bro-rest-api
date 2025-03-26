package com.example.gym_bro_rest_api.services.workoutplan;

import com.example.gym_bro_rest_api.entities.WorkoutPlan;

public interface WorkoutPlanQueryService {
    WorkoutPlan getWorkoutPlanById(Long id);
}
