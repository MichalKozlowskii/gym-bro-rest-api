package com.example.gym_bro_rest_api.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class WorkoutPlanDTO {
    private Long id;
    private String name;
    private Long userId;
    private List<ExerciseDTO> exercises;
    private List<SetsReps> setsReps;
    private LocalDateTime creationDate;
}
