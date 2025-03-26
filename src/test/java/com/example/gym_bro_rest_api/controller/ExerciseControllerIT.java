package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ExerciseControllerIT {
    @Autowired
    ExerciseController exerciseController;

    @Autowired
    ExerciseRepository exerciseRepository;

    User user1;
    User user2;

    @BeforeEach
    void setup() {
        user1 = User.builder()
                .id(1L)
                .username("test1")
                .password("password")
                .build();

        user2 = User.builder()
                .id(2L)
                .username("test2")
                .password("password2")
                .build();
    }

    Exercise saveTestExercise() {
        Exercise testExercise = Exercise.builder()
                .user(user1)
                .name("1")
                .build();

        return exerciseRepository.save(testExercise);
    }

    @Test
    void testListExercisesOfUser_Limit() {
        for (int i = 0; i < 1001; i++) {
            saveTestExercise();
        }

        Page<ExerciseDTO> dtos = exerciseController.listExercisesOfUser(user1, 1, 1001);

        assertThat(dtos.getContent().size()).isEqualTo(1000);
    }

    @Test
    void testListExercisesOfUser_20Exercises1Page() {
        for (int i = 0; i < 20; i++) {
            saveTestExercise();
        }

        Page<ExerciseDTO> dtos = exerciseController.listExercisesOfUser(user1, 1, 20);

        assertThat(dtos.getContent().size()).isEqualTo(20);
    }

    @Test
    void testDeleteExerciseById_NoAccess() {
        Exercise exercise = saveTestExercise();

        assertThrows(NoAccessException.class, () -> {
           exerciseController.deleteExerciseById(exercise.getId(), user2);
        });
    }

    @Test
    void testDeleteExerciseById_NotFound() {
        assertThrows(NotFoundException.class, () -> {
           exerciseController.deleteExerciseById(1321414L, user1);
        });
    }

    @Test
    void testDeleteExerciseById() {
        Exercise existing = saveTestExercise();

        ResponseEntity response = exerciseController.deleteExerciseById(existing.getId(), user1);

        assertThat(exerciseRepository.existsById(existing.getId())).isEqualTo(false);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testUpdateExerciseById_NoAccess() {
        Exercise existing = saveTestExercise();

        ExerciseDTO updateDto = ExerciseDTO.builder()
                .name("2")
                .demonstrationUrl("fakpofkaepof")
                .build();

        assertThrows(NoAccessException.class, () -> {
           exerciseController.updateExerciseById(existing.getId(), updateDto, user2);
        });
    }

    @Test
    void testUpdateExerciseById_NotFound() {
        ExerciseDTO updateDto = ExerciseDTO.builder()
                .name("2")
                .demonstrationUrl("fakpofkaepof")
                .build();

        assertThrows(NotFoundException.class, () -> {
            exerciseController.updateExerciseById(1321049L, updateDto, user1);
        });
    }

    @Test
    void testUpdateExerciseById() {
        Exercise existing = saveTestExercise();

        ExerciseDTO updateDto = ExerciseDTO.builder()
                .name("2")
                .demonstrationUrl("fakpofkaepof")
                .build();

        ResponseEntity response = exerciseController.updateExerciseById(existing.getId(), updateDto, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Exercise updated = exerciseRepository.findById(existing.getId()).orElse(null);

        assertThat(updated.getName()).isEqualTo(updateDto.getName());
        assertThat(updated.getDemonstrationUrl()).isEqualTo(updateDto.getDemonstrationUrl());
    }

    @Test
    void testGetExerciseById_NoAccess() {
        Exercise exercise = saveTestExercise();
        assertThrows(NoAccessException.class, () -> {
            exerciseController.getExerciseById(exercise.getId(), user2);
        });
    }

    @Test
    void testGetExerciseById_NotFound() {
        assertThrows(NotFoundException.class, () -> {
            exerciseController.getExerciseById(1321313L, user1);
        });
    }

    @Test
    void testGetExerciseById() {
        Exercise exercise = saveTestExercise();

        ExerciseDTO exerciseDto =exerciseController.getExerciseById(exercise.getId(), user1);

        assertThat(exerciseDto).isNotNull();
        assertThat(exerciseDto.getName()).isEqualTo("1");
        assertThat(exerciseDto.getUserId()).isNotNull();
    }

    @Test
    void testCreateNewExercise_NameOK() {
        ExerciseDTO testDto = ExerciseDTO.builder()
                .name("exercise1")
                .build();

        ResponseEntity<Map<String, String>> response = exerciseController.createNewExercise(testDto, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();

        String[] location = response.getHeaders().getLocation().getPath().split("/");
        Long savedId = Long.valueOf(location[3]);

        assertThat(exerciseRepository.findById(savedId)).isNotNull();
    }

    @Test
    void testCreateNewExercise_BadName() {
        ExerciseDTO testDto = ExerciseDTO.builder()
                .name("")
                .build();

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            exerciseController.createNewExercise(testDto, user1);
        });

        System.out.println(exception.getMessage());
    }
}