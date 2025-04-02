package com.example.gym_bro_rest_api.model.workout;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutCreationDTO {
    @NotNull(message = "Workout plan ID must not be null")
    private Long workoutPlanId;
}
