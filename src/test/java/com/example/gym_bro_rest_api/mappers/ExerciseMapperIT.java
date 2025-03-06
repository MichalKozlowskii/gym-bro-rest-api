package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ExerciseMapperIT {
    private final ExerciseMapper exerciseMapper = Mappers.getMapper(ExerciseMapper.class);

    @Mock
    private UserRepository userRepository;

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