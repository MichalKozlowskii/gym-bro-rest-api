package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.NoAccessException;
import com.example.gym_bro_rest_api.controller.NotFoundException;
import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.mappers.ExerciseSetMapper;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;
import com.example.gym_bro_rest_api.repositories.WorkoutRepository;
import com.example.gym_bro_rest_api.services.workoutplan.WorkoutPlanQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl implements WorkoutService {
    private final WorkoutPlanQueryService workoutPlanQueryService;
    private final WorkoutRepository workoutRepository;
    private final ExerciseSetService exerciseSetService;
    private final ExerciseSetMapper exerciseSetMapper;
    @Override
    public Long saveNewWorkout(WorkoutCreationDTO workoutCreationDTO, User user) {
        WorkoutPlan workoutPlan = workoutPlanQueryService.getWorkoutPlanById(workoutCreationDTO.getWorkoutPlanId());

        if (!workoutPlan.getUser().equals(user)) {
            throw new NoAccessException();
        }

        Workout workout = workoutRepository.save(Workout.builder()
                .workoutPlan(workoutPlan)
                .user(user)
                .build());

        return workout.getId();
    }

    @Override
    public Workout addNewSet(Long workoutId, ExerciseSetDTO exerciseSetDTO, User user) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(NotFoundException::new);

        if (!workout.getUser().equals(user)) {
            throw new NoAccessException();
        }

        ExerciseSet exerciseSet = exerciseSetService.saveNewExerciseSet(exerciseSetDTO, workout);

        List<ExerciseSet> sets = workout.getSets();
        sets.add(exerciseSet);
        workout.setSets(sets);

        return workoutRepository.save(workout);
    }

    @Override
    public Boolean deleteSet(Long workoutId, Long setId, User user) {
        Workout workout = workoutRepository.findById(workoutId).orElseThrow(NotFoundException::new);

        if (!workout.getUser().equals(user)) {
            throw new NoAccessException();
        }

        ExerciseSet exerciseSet = exerciseSetService.getExerciseSetById(setId).orElseThrow(NotFoundException::new);

        if (!exerciseSet.getUser().equals(user)) {
            throw new NoAccessException();
        }

        if (!exerciseSet.getWorkout().equals(workout)) {
            return false;
        }

        List<ExerciseSet> sets = workout.getSets();
        if (!sets.removeIf(set -> set.equals(exerciseSet))) {
            return false;
        }

        workout.setSets(sets);
        Workout updatedWorkout = workoutRepository.save(workout);

        return exerciseSetService.deleteSetById(setId);
    }
}
