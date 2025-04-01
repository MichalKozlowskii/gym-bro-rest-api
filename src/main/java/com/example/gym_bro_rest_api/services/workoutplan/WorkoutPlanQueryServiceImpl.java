package com.example.gym_bro_rest_api.services.workoutplan;

import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkoutPlanQueryServiceImpl implements WorkoutPlanQueryService {
    private final WorkoutPlanrepository workoutPlanrepository;
    @Override
    public WorkoutPlan getWorkoutPlanById(Long id) {
        return workoutPlanrepository.findById(id).orElseThrow(NotFoundException::new);
    }
}
