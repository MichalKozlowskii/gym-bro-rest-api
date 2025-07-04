package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.*;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import com.example.gym_bro_rest_api.repositories.WorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class WorkoutServiceImplUnitTest {
    @Mock
    private WorkoutPlanrepository workoutPlanrepository;
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
                .build();
    }

    @Test
    void testSaveNewWorkout_Success() {
        given(workoutPlanrepository.findById(any(Long.class))).willReturn(Optional.of(workoutPlan));
        given(workoutRepository.save(any(Workout.class))).willReturn(Workout.builder()
                .id(1L)
                .user(user)
                .workoutPlan(workoutPlan)
                .build());

        Long result = workoutService.saveNewWorkout(WorkoutCreationDTO.builder()
                .workoutPlanId(workoutPlan.getId())
                .build(), user);

        assertThat(result).isNotNull();
        verify(workoutRepository, times(1)).save(any(Workout.class));
    }

    @Test
    void testSaveNewWorkout_WorkoutPlanNotFound() {
        given(workoutPlanrepository.findById(any(Long.class))).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                workoutService.saveNewWorkout(workoutCreationDTO, user));

        verify(workoutRepository, never()).save(any());
    }

    @Test
    void testSaveNewWorkout_NoAccessToWrokoutPlan() {
        User anotherUser = User.builder().id(2L).build();
        WorkoutPlan anotherWorkoutPlan = WorkoutPlan.builder().id(2L).user(anotherUser).build();

        given(workoutPlanrepository.findById(any(Long.class))).willReturn(Optional.of(anotherWorkoutPlan));

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

    @Test
    void testDeleteSet_Success() {
        Workout savedWorkout = Workout.builder()
                .id(1L)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .user(user)
                .build();

        ExerciseSet savedSet = ExerciseSet.builder()
                .id(1L)
                .weight(50.)
                .reps(8)
                .exercise(Exercise.builder().id(1L).name("bench press").build())
                .workout(savedWorkout)
                .user(user)
                .build();

        savedWorkout.getSets().add(savedSet);

        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(savedWorkout));
        given(exerciseSetService.getExerciseSetById(any(Long.class))).willReturn(Optional.of(savedSet));

        given(workoutRepository.save(any(Workout.class))).willAnswer(invocation -> {
            Workout updatedWorkout = invocation.getArgument(0);
            return updatedWorkout;
        });

        given(exerciseSetService.deleteSetById(any(Long.class))).willReturn(true);

        workoutService.deleteSet(savedWorkout.getId(), savedSet.getId(), user);

        assertFalse(savedWorkout.getSets().contains(savedSet));

        verify(workoutRepository, times(1)).findById(any(Long.class));
        verify(exerciseSetService, times(1)).getExerciseSetById(any(Long.class));
        verify(workoutRepository, times(1)).save(any(Workout.class));
    }

    @Test
    void testDeleteSet_SetNotFromThisWorkout() {
        Workout savedWorkout = Workout.builder()
                .id(1L)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .user(user)
                .build();

        Workout anotherWorkout = Workout.builder()
                .id(2L)
                .workoutPlan(WorkoutPlan.builder().build())
                .sets(new ArrayList<>())
                .user(user)
                .build();

        ExerciseSet savedSet = ExerciseSet.builder()
                .id(1L)
                .weight(50.)
                .reps(8)
                .exercise(Exercise.builder().id(1L).name("bench press").build())
                .workout(anotherWorkout)
                .user(user)
                .build();

        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(savedWorkout));
        given(exerciseSetService.getExerciseSetById(any(Long.class))).willReturn(Optional.of(savedSet));

        Boolean result = workoutService.deleteSet(111L, 111L, user);

        assertThat(result).isFalse();

        verify(workoutRepository, times(1)).findById(any(Long.class));
        verify(exerciseSetService, times(1)).getExerciseSetById(any(Long.class));
        verify(workoutRepository, never()).save(any(Workout.class));
        verify(exerciseSetService, never()).deleteSetById(any(Long.class));
    }

    @Test
    void testDeleteSet_WorkoutNotFound() {
        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> workoutService.deleteSet(111L, 111L, user));
    }

    @Test
    void testDeleteSet_SetNotFound() {
        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(Workout.builder()
                .user(user)
                .build()));

        given(exerciseSetService.getExerciseSetById(any(Long.class))).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> workoutService.deleteSet(111L, 111L, user));
    }

    @Test
    void testDeleteSet_NoAccessToWorkout() {
        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(Workout.builder()
                .user(User.builder().id(1000L).build())
                .build()));

        assertThrows(NoAccessException.class, () -> workoutService.deleteSet(111L, 111L, user));
    }

    @Test
    void testDeleteSet_NoAccessToSet() {
        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(Workout.builder()
                .user(user)
                .build()));

        given(exerciseSetService.getExerciseSetById(any(Long.class))).willReturn(Optional.of(ExerciseSet.builder()
                .user(User.builder().id(1000L).build())
                .build()));

        assertThrows(NoAccessException.class, () -> workoutService.deleteSet(111L, 111L, user));
    }

    @Test
    void testDeleteWorkout_DeleteSets_Success() {
        Workout savedWorkout = Workout.builder()
                .id(1L)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .user(user)
                .build();

        ExerciseSet savedSet = ExerciseSet.builder()
                .id(1L)
                .weight(50.)
                .reps(8)
                .exercise(Exercise.builder().id(1L).name("bench press").build())
                .workout(savedWorkout)
                .user(user)
                .build();

        savedWorkout.getSets().add(savedSet);

        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(savedWorkout));
        given(exerciseSetService.deleteSetById(any(Long.class))).willReturn(true);

        workoutService.deleteWorkoutById(savedWorkout.getId(), user);

        verify(exerciseSetService, times(1)).deleteSetById(savedSet.getId());
        verify(workoutRepository, times(1)).delete(savedWorkout);
    }

    @Test
    void testDeleteWorkout_NoSetsToDelete_Success() {
        Workout savedWorkout = Workout.builder()
                .id(1L)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .user(user)
                .build();

        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(savedWorkout));
        given(exerciseSetService.deleteSetById(any(Long.class))).willReturn(true);

        workoutService.deleteWorkoutById(savedWorkout.getId(), user);

        verify(exerciseSetService, never()).deleteSetById(any(Long.class));
        verify(workoutRepository, times(1)).delete(savedWorkout);
    }

    @Test
    void testDeleteWorkout_NotFound() {
        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> workoutService.deleteWorkoutById(12313L, user));
    }

    @Test
    void testDeleteWorkout_NotAccess() {
        Workout savedWorkout = Workout.builder()
                .id(1L)
                .workoutPlan(workoutPlan)
                .sets(new ArrayList<>())
                .user(user)
                .build();

        given(workoutRepository.findById(any(Long.class))).willReturn(Optional.of(savedWorkout));

        assertThrows(NoAccessException.class, () ->
                workoutService.deleteWorkoutById(savedWorkout.getId(), User.builder().build()));
    }
}