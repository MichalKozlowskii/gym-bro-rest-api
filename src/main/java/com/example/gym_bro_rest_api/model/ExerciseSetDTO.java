package com.example.gym_bro_rest_api.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ExerciseSetDTO {
   private Long id;
   private ExerciseDTO exercise;
   private Long workoutId;
   private Long userId;
   private Double weight;
   private Integer reps;
   private LocalDateTime creationDate;
}
