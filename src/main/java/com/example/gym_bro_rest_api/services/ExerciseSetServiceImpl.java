package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.ExerciseSetRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExerciseSetServiceImpl implements ExerciseSetService {
    private final ExerciseSetRepository exerciseSetRepository;
    private final ExerciseRepository exerciseRepository;
    @Override
    public ExerciseSet saveNewExerciseSet(ExerciseSetDTO exerciseSetDTO, Workout workout) throws BadRequestException {
        Exercise exercise = exerciseRepository.findById(exerciseSetDTO.getExercise().getId())
                .orElseThrow(NotFoundException::new);

        if (!exercise.getUser().getId().equals(workout.getUser().getId())) {
            throw new NoAccessException();
        }

        boolean isInWorkoutPlan = workout.getWorkoutPlan().getExercises().stream()
                .anyMatch(e -> e.getId().equals(exercise.getId()));

        if (!isInWorkoutPlan) {
            throw new BadRequestException("The exercise is not part of the workout plan");
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
