package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.*;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExerciseSetMapperIT {
    @Autowired
    private ExerciseSetMapper exerciseSetMapper;

    @Test
    void exerciseSetToExerciseSetDTO() {
        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("dasdsada")
                .build();

        Exercise exercise = Exercise.builder()
                .id(1L)
                .name("bench press")
                .user(user)
                .build();

        Workout workout = Workout.builder()
                .id(1L)
                .user(user)
                .build();

        ExerciseSet exerciseSet = ExerciseSet.builder()
                .exercise(exercise)
                .user(user)
                .workout(workout)
                .reps(8)
                .weight(50.0)
                .build();

        ExerciseSetDTO exerciseSetDTO = exerciseSetMapper.exerciseSetToExerciseSetDTO(exerciseSet);

        System.out.println(exerciseSetDTO.toString());

        assertThat(exerciseSetDTO.getExercise()).isNotNull();
        assertThat(exerciseSetDTO.getExercise().getId()).isEqualTo(exercise.getId());
        assertThat(exerciseSetDTO.getExercise().getName()).isEqualTo(exercise.getName());

        assertThat(exerciseSetDTO.getUserId()).isEqualTo(user.getId());

        assertThat(exerciseSetDTO.getWorkoutId()).isEqualTo(workout.getId());

        assertThat(exerciseSetDTO.getReps()).isEqualTo(exerciseSet.getReps());
        assertThat(exerciseSetDTO.getWeight()).isEqualTo(exerciseSet.getWeight());
    }
}