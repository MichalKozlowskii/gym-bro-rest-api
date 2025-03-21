package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.mappers.ExerciseSetMapper;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseSetRepository;
import com.example.gym_bro_rest_api.services.exercise.ExerciseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseSetServiceImpl implements ExerciseSetService {
    private final ExerciseSetRepository exerciseSetRepository;
    private final ExerciseQueryService exerciseQueryService;
    @Override
    public ExerciseSet saveNewExerciseSet(ExerciseSetDTO exerciseSetDTO, Workout workout) {
        return exerciseSetRepository.save(ExerciseSet.builder()
                        .exercise(exerciseQueryService.getExerciseById(exerciseSetDTO.getExercise().getId()))
                        .user(workout.getUser())
                        .workout(workout)
                        .weight(exerciseSetDTO.getWeight())
                        .reps(exerciseSetDTO.getReps())
                .build());
    }
}
