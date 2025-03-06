package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ExerciseMapperIT {
    private final ExerciseMapper exerciseMapper = Mappers.getMapper(ExerciseMapper.class);

    private User testUser = User.builder()
            .id(1L)
            .username("testUser")
            .password("password")
            .enabled(true)
            .build();

    @Test
    void testExerciseToExerciseDto() {
        Exercise exercise = Exercise.builder()
                .id(1L)
                .name("exercise1")
                .demonstrationUrl("afafako")
                .user(testUser)
                .build();

        ExerciseDTO exerciseDTO = exerciseMapper.exerciseToExerciseDto(exercise);

        assertThat(exerciseDTO.getName()).isEqualTo("exercise1");
        assertThat(exerciseDTO.getDemonstrationUrl()).isEqualTo(exercise.getDemonstrationUrl());
        assertThat(exerciseDTO.getId()).isEqualTo(exercise.getId());
        assertThat(exerciseDTO.getUserId()).isNotNull();
    }

    @Test
    void testExerciseDtoToExercise() {
        ExerciseDTO exerciseDTO = ExerciseDTO.builder()
                .id(1L)
                .name("exercise 1")
                .demonstrationUrl("dsadsad")
                .userId(1L)
                .build();

        Exercise exercise = exerciseMapper.exerciseDtoToExercise(exerciseDTO);

        assertThat(exercise.getName()).isEqualTo("exercise 1");
        assertThat(exercise.getDemonstrationUrl()).isEqualTo(exerciseDTO.getDemonstrationUrl());
        assertThat(exercise.getId()).isEqualTo(exerciseDTO.getId());
        assertThat(exercise.getUser()).isNotNull();
        assertThat(exercise.getUser()).isInstanceOfAny(User.class);
        assertThat(exercise.getUser().getId()).isEqualTo(exerciseDTO.getUserId());
    }
}