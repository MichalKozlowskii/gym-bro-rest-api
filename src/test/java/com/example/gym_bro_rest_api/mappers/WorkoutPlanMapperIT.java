package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import com.example.gym_bro_rest_api.model.SetsReps;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WorkoutPlanMapperIT {
    @Autowired
    private WorkoutPlanMapper workoutPlanMapper;

    @Test
    void testWorkoutPlanToWorkoutDto() {
        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("dasdsada")
                .build();

        List<Exercise> exercises = new ArrayList<>();
        List<SetsReps> setsReps = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            exercises.add(Exercise.builder().build());
            setsReps.add(new SetsReps(3, 8));
        }

        WorkoutPlan workoutPlan = WorkoutPlan.builder()
                .id(1L)
                .name("workout plan 1")
                .user(user)
                .exercises(exercises)
                .setsReps(setsReps)
                .build();

        WorkoutPlanDTO workoutPlanDTO = workoutPlanMapper.workoutPlanToWorkoutPlanDto(workoutPlan);

        System.out.println(workoutPlanDTO.toString());

        assertThat(workoutPlanDTO).isNotNull();
        assertThat(workoutPlanDTO.getExercises().size()).isEqualTo(6);
        assertThat(workoutPlanDTO.getSetsReps().size()).isEqualTo(6);
        assertThat(workoutPlanDTO.getUserId()).isEqualTo(1L);
    }
}