package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.mappers.WorkoutPlanMapper;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import com.example.gym_bro_rest_api.services.exercise.ExerciseQueryService;
import com.example.gym_bro_rest_api.services.workoutplan.WorkoutPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class WorkoutPlanServiceImplUnitTest {

    @Mock
    private WorkoutPlanMapper workoutPlanMapper;

    @Mock
    private WorkoutPlanrepository workoutPlanrepository;

    @Mock
    private ExerciseQueryService exerciseQueryService;

    @InjectMocks
    private WorkoutPlanServiceImpl workoutPlanService;

    private User user;
    private Exercise exercise;
    private ExerciseDTO exerciseDTO;
    private WorkoutPlan workoutPlan;
    private WorkoutPlanDTO workoutPlanDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).build();
        exercise = Exercise.builder().id(1L).user(user).build();
        exerciseDTO = ExerciseDTO.builder().id(1L).build();

        workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("Leg Day")
                .exercises(List.of(exerciseDTO))
                .setsReps(List.of())
                .build();

        workoutPlan = WorkoutPlan.builder()
                .name("Leg Day")
                .exercises(List.of(exercise))
                .setsReps(List.of())
                .user(user)
                .build();
    }

    @Test
    void saveNewWorkoutPlan_Success() {
        given(exerciseQueryService.getExerciseById(1L)).willReturn(exercise);
        given(workoutPlanrepository.save(any(WorkoutPlan.class))).willReturn(workoutPlan);
        given(workoutPlanMapper.workoutPlanToWorkoutPlanDto(any(WorkoutPlan.class)))
                .willReturn(workoutPlanDTO);

        WorkoutPlanDTO result = workoutPlanService.saveNewWorkoutPlan(workoutPlanDTO, user);

        assertNotNull(result);
        assertEquals("Leg Day", result.getName());
        verify(workoutPlanrepository, times(1)).save(any(WorkoutPlan.class));
    }

    @Test
    void saveNewWorkoutPlan_ExerciseNotFound() {
        given(exerciseQueryService.getExerciseById(1L)).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () ->
                workoutPlanService.saveNewWorkoutPlan(workoutPlanDTO, user));

        verify(workoutPlanrepository, never()).save(any());
    }

    @Test
    void saveNewWorkoutPlan_NoAccessToExercise() {
        User anotherUser = User.builder().id(2L).build();
        Exercise anotherExercise = Exercise.builder().id(1L).user(anotherUser).build();
        given(exerciseQueryService.getExerciseById(1L)).willReturn(anotherExercise);

        assertThrows(NoAccessException.class, () ->
                workoutPlanService.saveNewWorkoutPlan(workoutPlanDTO, user));

        verify(workoutPlanrepository, never()).save(any());
    }
}