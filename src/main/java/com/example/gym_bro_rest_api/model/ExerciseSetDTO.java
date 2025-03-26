package com.example.gym_bro_rest_api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ExerciseSetDTO {
   private Long id;
   @NotNull
   private ExerciseDTO exercise;
   private Long workoutId;
   private Long userId;
   @NotNull
   private Double weight;
   @NotNull
   private Integer reps;
   private LocalDateTime creationDate;
}
