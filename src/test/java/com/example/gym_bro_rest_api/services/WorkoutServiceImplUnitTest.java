package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.NoAccessException;
import com.example.gym_bro_rest_api.controller.NotFoundException;
import com.example.gym_bro_rest_api.entities.*;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @Mock
    private ExerciseSetService exerciseSetService;

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

    @Test
    void testAddNewSet() {
        Workout savedWorkout = Workout.builder()
                .id(1L)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .user(user)
                .build();

        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(savedWorkout));

        ExerciseSet savedSet = ExerciseSet.builder()
                .id(1L)
                .weight(50.)
                .reps(8)
                .exercise(Exercise.builder().id(1L).name("bench press").build())
                .workout(savedWorkout)
                .build();

        given(exerciseSetService.saveNewExerciseSet(any(ExerciseSetDTO.class), any(Workout.class))).willReturn(savedSet);

        List<ExerciseSet> sets = savedWorkout.getSets();
        sets.add(savedSet);
        savedWorkout.setSets(sets);

        given(workoutRepository.save(any(Workout.class))).willReturn(savedWorkout);

        Workout updatedWorkout = workoutService.addNewSet(savedWorkout.getId(), ExerciseSetDTO.builder().build(), user);

        assertThat(updatedWorkout.getSets()).isNotEmpty();
        assertThat(updatedWorkout.getSets().get(1).getWorkout().getId()).isEqualTo(savedWorkout.getId());
    }

    @Test
    void testAddNewSet_NoAccessToWorkout() {
        User anotherUser = User.builder().id(2L).build();
        Workout savedWorkout = Workout.builder()
                .id(1L)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .user(user)
                .build();

        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(savedWorkout));

        assertThrows(NoAccessException.class, () ->
                workoutService.addNewSet(1L, ExerciseSetDTO.builder().build(), anotherUser));
    }

    @Test
    void testAddNewSet_WorkoutNotFound() {
        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                workoutService.addNewSet(1L, ExerciseSetDTO.builder().build(), user));
    }
}