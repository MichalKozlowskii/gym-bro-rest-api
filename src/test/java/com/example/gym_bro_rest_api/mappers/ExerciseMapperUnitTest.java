package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ExerciseMapperUnitTest {
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
}