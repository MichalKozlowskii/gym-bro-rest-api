package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutViewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WorkoutMapperImpl implements WorkoutMapper {
    private final WorkoutPlanMapper workoutPlanMapper;
    private final ExerciseSetMapper exerciseSetMapper;

    @Override
    public WorkoutViewDTO workoutToWorkoutViewDTO(Workout workout) {
        if (workout == null) return null;

        WorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.workoutPlanToWorkoutPlanDto(workout.getWorkoutPlan());
        Map<ExerciseDTO, List<ExerciseSetDTO>> exerciseSetMap = mapExerciseSets(workoutPlanDTO, workout.getSets());

        return WorkoutViewDTO.builder()
                .id(workout.getId())
                .workoutPlanDTO(workoutPlanDTO)
                .userId(workout.getUser().getId())
                .exerciseSetMap(exerciseSetMap)
                .creationDate(workout.getCreationDate())
                .build();
    }

    private Map<ExerciseDTO, List<ExerciseSetDTO>> mapExerciseSets(WorkoutPlanDTO workoutPlanDTO, List<ExerciseSet> sets) {
        Map<ExerciseDTO, List<ExerciseSetDTO>> exerciseSetMap = new HashMap<>();

        Map<Long, ExerciseDTO> exerciseDTOMap = workoutPlanDTO.getExercises().stream()
                .collect(Collectors.toMap(ExerciseDTO::getId, e -> e));

        for (ExerciseSet set : sets) {
            ExerciseDTO exerciseDTO = exerciseDTOMap.get(set.getExercise().getId());
            if (exerciseDTO != null) {
                exerciseSetMap.computeIfAbsent(exerciseDTO, k -> new ArrayList<>())
                        .add(exerciseSetMapper.exerciseSetToExerciseSetDTO(set));
            }
        }

        return exerciseSetMap;
    }
}
