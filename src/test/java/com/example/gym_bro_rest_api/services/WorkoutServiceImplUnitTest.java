package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.NoAccessException;
import com.example.gym_bro_rest_api.controller.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import com.example.gym_bro_rest_api.repositories.WorkoutRepository;
import com.example.gym_bro_rest_api.services.workoutplan.WorkoutPlanQueryService;
import com.example.gym_bro_rest_api.services.workoutplan.WorkoutPlanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class WorkoutServiceImplUnitTest {
    @Mock
    private WorkoutPlanQueryService workoutPlanQueryService;
    @Mock
    private WorkoutRepository workoutRepository;

    @InjectMocks
    private WorkoutServiceImpl workoutService;

    WorkoutPlan workoutPlan;
    User user;
    WorkoutCreationDTO workoutCreationDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder().id(1L).build();
        workoutPlan = WorkoutPlan.builder().id(1L).user(user).build();

        workoutCreationDTO = WorkoutCreationDTO.builder()
                .workoutPlanId(workoutPlan.getId())
                .userId(user.getId())
                .build();
    }

    @Test
    void testSaveNewWorkout_Success() {
        given(workoutPlanQueryService.getWorkoutPlanById(any(Long.class))).willReturn(workoutPlan);
        given(workoutRepository.save(any(Workout.class))).willReturn(Workout.builder()
                .id(1L)
                .user(user)
                .workoutPlan(workoutPlan)
                .build());

        Long result = workoutService.saveNewWorkout(WorkoutCreationDTO.builder()
                .workoutPlanId(workoutPlan.getId())
                .userId(user.getId())
                .build(), user);

        assertThat(result).isNotNull();
        verify(workoutRepository, times(1)).save(any(Workout.class));
    }

    @Test
    void testSaveNewWorkout_WorkoutPlanNotFound() {
        given(workoutPlanQueryService.getWorkoutPlanById(any(Long.class))).willThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () ->
                workoutService.saveNewWorkout(workoutCreationDTO, user));

        verify(workoutRepository, never()).save(any());
    }

    @Test
    void testSaveNewWorkout_NoAccessToWrokoutPlan() {
        User anotherUser = User.builder().id(2L).build();
        WorkoutPlan anotherWorkoutPlan = WorkoutPlan.builder().id(2L).user(anotherUser).build();

        given(workoutPlanQueryService.getWorkoutPlanById(any(Long.class))).willReturn(anotherWorkoutPlan);

        assertThrows(NoAccessException.class, () ->
                workoutService.saveNewWorkout(workoutCreationDTO, user));

        verify(workoutRepository, never()).save(any());
    }
}