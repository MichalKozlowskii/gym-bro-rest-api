package com.example.gym_bro_rest_api.model.workout;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WorkoutCreationDTO {
    @NotNull(message = "Workout plan ID must not be null")
    private Long workoutPlanId;
    private Long userId;
}
