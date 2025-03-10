package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.NoAccessException;
import com.example.gym_bro_rest_api.controller.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.mappers.ExerciseMapper;
import com.example.gym_bro_rest_api.mappers.WorkoutPlanMapper;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutPlanServiceImpl implements WorkoutPlanService {
    private final WorkoutPlanMapper workoutPlanMapper;
    private final WorkoutPlanrepository workoutPlanrepository;
    private final ExerciseRepository exerciseRepository;
    @Override
    public WorkoutPlanDTO saveNewWorkoutPlan(WorkoutPlanDTO workoutPlanDTO, User user) {
        List<Exercise> exercises = workoutPlanDTO.getExercises().stream()
                .map(exerciseDTO -> {
                    Exercise exercise = exerciseRepository.findById(exerciseDTO.getId())
                            .orElseThrow(NotFoundException::new);

                    if (!exercise.getUser().getId().equals(user.getId())) {
                        throw new NoAccessException();
                    }

                    return exercise;
                })
                .toList();

        WorkoutPlan workoutPlan = WorkoutPlan.builder()
                .name(workoutPlanDTO.getName())
                .exercises(exercises)
                .setsReps(workoutPlanDTO.getSetsReps())
                .user(user)
                .build();

        return workoutPlanMapper.workoutPlanToWorkoutPlanDto(workoutPlanrepository.save(workoutPlan));
    }

    @Override
    public Optional<WorkoutPlanDTO> getWorkoutPlanById(Long id) {
        return workoutPlanrepository.findById(id)
                .map(workoutPlanMapper::workoutPlanToWorkoutPlanDto);
    }
}
