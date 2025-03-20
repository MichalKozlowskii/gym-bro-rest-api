package com.example.gym_bro_rest_api.model.workout;

import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class WorkoutViewDTO {
    private Long id;
    private WorkoutPlanDTO workoutPlanDTO;
    private Map<ExerciseDTO, List<ExerciseSetDTO>> exerciseSetMap = new HashMap<>();
    private Long userId;
    private LocalDateTime creationDate;
}
