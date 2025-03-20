package com.example.gym_bro_rest_api.model.workout;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WorkoutCreationDTO {
    private Long workoutPlanId;
    private Long userId;
}
