package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.services.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    UserService userService;

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

        ResponseEntity<Map<String, String>> response = exerciseController.createNewExercise(testDto, user1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotEmpty();
    }
}