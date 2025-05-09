package com.example.gym_bro_rest_api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ExerciseSetDTO {
   private Long id;
   @NotNull(message = "Exercise id must not be null.")
   private ExerciseDTO exercise;
   private Long workoutId;
   private Long userId;
   @NotNull(message = "Weight must not be null.")
   private Double weight;
   @NotNull(message = "Reps must not be null.")
   private Integer reps;
   private LocalDateTime creationDate;
}
