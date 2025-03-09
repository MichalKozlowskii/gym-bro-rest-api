package com.example.gym_bro_rest_api.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class WorkoutPlanDTO {
    private Long id;
    private String name;
    private Long userId;
    private List<ExerciseDTO> exercises;
    private List<SetsReps> exerciseDetails;
}
