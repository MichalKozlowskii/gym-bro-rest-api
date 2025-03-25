package com.example.gym_bro_rest_api.model.workout;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WorkoutCreationDTO {
    @NotNull
    private Long workoutPlanId;
    private Long userId;
}
