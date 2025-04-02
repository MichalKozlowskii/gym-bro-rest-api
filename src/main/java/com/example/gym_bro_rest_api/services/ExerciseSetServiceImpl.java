package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.mappers.ExerciseSetMapper;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseSetRepository;
import com.example.gym_bro_rest_api.services.exercise.ExerciseQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseSetServiceImpl implements ExerciseSetService {
    private final ExerciseSetRepository exerciseSetRepository;
    private final ExerciseQueryService exerciseQueryService;
    @Override
    public ExerciseSet saveNewExerciseSet(ExerciseSetDTO exerciseSetDTO, Workout workout) {
        Exercise exercise = exerciseQueryService.getExerciseById(exerciseSetDTO.getExercise().getId());

        if (!exercise.getUser().getId().equals(workout.getUser().getId())) {
            throw new NoAccessException();
        }

        return exerciseSetRepository.save(ExerciseSet.builder()
                        .exercise(exercise)
                        .user(workout.getUser())
                        .workout(workout)
                        .weight(exerciseSetDTO.getWeight())
                        .reps(exerciseSetDTO.getReps())
                .build());
    }

    @Override
    public Optional<ExerciseSet> getExerciseSetById(Long id) {
        return exerciseSetRepository.findById(id);
    }

    @Override
    public Boolean deleteSetById(Long id) {
        if (exerciseSetRepository.existsById(id)) {
            exerciseSetRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
