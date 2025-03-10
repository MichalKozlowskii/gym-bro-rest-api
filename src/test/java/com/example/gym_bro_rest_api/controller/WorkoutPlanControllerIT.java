package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.SetsReps;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import com.example.gym_bro_rest_api.repositories.WorkoutPlanrepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WorkoutPlanControllerIT {
    @Autowired
    WorkoutPlanController workoutPlanController;

    @Autowired
    WorkoutPlanrepository workoutPlanrepository;

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    UserRepository userRepository;

    User user1;
    User user2;
    Exercise testExercise;

    @BeforeEach
    void setup() {
        user1 = userRepository.save(User.builder()
                .username("test1")
                .password("password")
                .build());

        user2 = userRepository.save(User.builder()
                .username("test2")
                .password("password2")
                .build());

        testExercise = exerciseRepository.save(Exercise.builder()
                .user(user1)
                .name("1")
                .build());
    }

    WorkoutPlan saveTestWorkoutPlan() {
        return workoutPlanrepository.save(WorkoutPlan.builder()
                .name("test")
                .exercises(new ArrayList<>(List.of(testExercise)))
                .user(user1)
                .setsReps(new ArrayList<>(List.of(new SetsReps(3, 8))))
                .build());
    }

    @Test
    void testUpdateWorkoutPlanById_NoAccess() {
        WorkoutPlan workoutPlan = saveTestWorkoutPlan();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("dwdawda")
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        assertThrows(NoAccessException.class, () ->
                workoutPlanController.updateWorkoutPlanById(workoutPlan.getId(), workoutPlanDTO, user2));
    }

    @Test
    void testUpdateWorkoutPlanById_NotFound() {
        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("workoutplan")
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        System.out.println(workoutPlanDTO.getName());

        assertThrows(NotFoundException.class, () ->
                workoutPlanController.updateWorkoutPlanById(3421421L, workoutPlanDTO, user1));
    }

    @Test
    void testUpdateWorkoutPlanById_BadName() {
        WorkoutPlan workoutPlan = saveTestWorkoutPlan();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("")
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        assertThrows(BadNameException.class, () ->
                workoutPlanController.updateWorkoutPlanById(workoutPlan.getId(), workoutPlanDTO, user1));
    }

    @Test
    void testUpdateWorkoutPlanById_Success() {
        WorkoutPlan workoutPlan = saveTestWorkoutPlan();

        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(testExercise.getId())
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("test_updated")
                .exercises(List.of(testExerciseDto))
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        ResponseEntity<Map<String, String>> response = workoutPlanController.updateWorkoutPlanById(
                workoutPlan.getId(), workoutPlanDTO, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        WorkoutPlan updated = workoutPlanrepository.findById(workoutPlan.getId()).get();

        assertThat(updated.getName()).isEqualTo(workoutPlanDTO.getName());
    }

    @Test
    void testGetWorkoutPlanById_NoAccess() {
        WorkoutPlan saved = workoutPlanrepository.save(WorkoutPlan.builder().name("1").user(user1).build());

        assertThrows(NoAccessException.class, () -> workoutPlanController.getWorkoutPlanById(saved.getId(), user2));
    }

    @Test
    void testGetWorkoutPlanById_NotFound() {
        assertThrows(NotFoundException.class, () -> workoutPlanController.getWorkoutPlanById(13241L, user1));
    }

    @Test
    void testGetWorkoutPlanById_Success() {
        WorkoutPlan saved = workoutPlanrepository.save(WorkoutPlan.builder().name("1").user(user1).build());

        WorkoutPlanDTO workoutPlanDTO = workoutPlanController.getWorkoutPlanById(saved.getId(), user1);

        assertThat(workoutPlanDTO).isNotNull();
        assertThat(workoutPlanDTO.getUserId()).isEqualTo(user1.getId());
    }

    @Test
    void testSaveNewWorkoutPlan_ExerciseNoAccess() {
        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(testExercise.getId())
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("test")
                .exercises(List.of(testExerciseDto))
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        assertThrows(NoAccessException.class, () -> workoutPlanController.createNewWorkoutPlan(workoutPlanDTO, user2));
    }

    @Test
    void testSaveNewWorkoutPlan_ExerciseNotFound() {
        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(14214L)
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("test")
                .exercises(List.of(testExerciseDto))
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        assertThrows(NotFoundException.class, () -> workoutPlanController.createNewWorkoutPlan(workoutPlanDTO, user1));
    }

    @Test
    void testSaveNewWorkoutPlan_BadName() {
        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(testExercise.getId())
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("")
                .exercises(List.of(testExerciseDto))
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        assertThrows(BadNameException.class, () -> workoutPlanController.createNewWorkoutPlan(workoutPlanDTO, user1));
    }

    @Test
    void testSaveNewWorkoutPlan_Success() {
        ExerciseDTO testExerciseDto = ExerciseDTO.builder()
                .id(testExercise.getId())
                .build();

        WorkoutPlanDTO workoutPlanDTO = WorkoutPlanDTO.builder()
                .name("workout_plan1")
                .exercises(List.of(testExerciseDto))
                .setsReps(List.of(new SetsReps(3, 8)))
                .build();

        ResponseEntity<Map<String, String>> response = workoutPlanController.createNewWorkoutPlan(workoutPlanDTO, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] location = response.getHeaders().getLocation().getPath().split("/");
        Long savedId = Long.valueOf(location[3]);

        assertThat(workoutPlanrepository.findById(savedId)).isNotNull();
    }
}