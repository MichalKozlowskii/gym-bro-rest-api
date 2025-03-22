package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.entities.*;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutViewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WorkoutMapperIT {
    @Autowired
    private WorkoutMapper workoutMapper;
    private Workout workout;
    private WorkoutPlan workoutPlan;
    private User user;
    private ExerciseSet exerciseSet;
    private ExerciseSet exerciseSet3;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).build();

        Exercise exercise = new Exercise();
        exercise.setId(10L);
        exercise.setName("Bench Press");
        exercise.setUser(user);

        exerciseSet = new ExerciseSet();
        exerciseSet.setId(100L);
        exerciseSet.setExercise(exercise);
        exerciseSet.setReps(10);
        exerciseSet.setWeight(80.0);
        exerciseSet.setUser(user);

        ExerciseSet exerciseSet2 = new ExerciseSet();
        exerciseSet2.setId(101L);
        exerciseSet2.setExercise(exercise);
        exerciseSet2.setReps(10);
        exerciseSet2.setWeight(80.0);
        exerciseSet2.setUser(user);

        Exercise exercise2 = new Exercise();
        exercise2.setId(11L);
        exercise2.setName("Dumbell Row");
        exercise2.setUser(user);

        exerciseSet3 = new ExerciseSet();
        exerciseSet3.setId(103L);
        exerciseSet3.setExercise(exercise2);
        exerciseSet3.setReps(10);
        exerciseSet3.setWeight(20.0);
        exerciseSet3.setUser(user);

        workoutPlan = new WorkoutPlan();
        workoutPlan.setId(500L);
        workoutPlan.setExercises(List.of(exercise, exercise2));
        workoutPlan.setUser(user);

        workout = new Workout();
        workout.setId(200L);
        workout.setWorkoutPlan(workoutPlan);
        workout.setUser(user);
        workout.setSets(List.of(exerciseSet, exerciseSet2, exerciseSet3));

        exerciseSet.setWorkout(workout);
        exerciseSet2.setWorkout(workout);
        exerciseSet3.setWorkout(workout);
    }

    @Test
    void testWorkoutToWorkoutViewDTO() {
        WorkoutViewDTO dto = workoutMapper.workoutToWorkoutViewDTO(workout);

        assertNotNull(dto);
        assertEquals(workout.getId(), dto.getId());
        assertEquals(user.getId(), dto.getUserId());
        assertEquals(workoutPlan.getId(), dto.getWorkoutPlanDTO().getId());

        assertEquals(dto.getWorkoutPlanDTO().getId(), workoutPlan.getId());

        // Validate exerciseSetMap
        Map<ExerciseDTO, List<ExerciseSetDTO>> exerciseSetMap = dto.getExerciseSetMap();
        assertNotNull(exerciseSetMap);
        assertEquals(2, exerciseSetMap.size());

        ExerciseDTO exerciseDTO = dto.getWorkoutPlanDTO().getExercises().get(0);
        assertEquals(exerciseDTO.getUserId(), user.getId());

        assertTrue(exerciseSetMap.containsKey(exerciseDTO));
        assertEquals(2, exerciseSetMap.get(exerciseDTO).size());
        assertEquals(exerciseSet.getId(), exerciseSetMap.get(exerciseDTO).get(0).getId());
        assertEquals(exerciseSet.getWorkout().getId(), exerciseSetMap.get(exerciseDTO).get(0).getWorkoutId());
        assertEquals(exerciseSet.getUser().getId(), exerciseSetMap.get(exerciseDTO).get(0).getUserId());

        ExerciseDTO exerciseDTO2 = dto.getWorkoutPlanDTO().getExercises().get(1);
        assertEquals(exerciseDTO2.getUserId(), user.getId());

        assertTrue(exerciseSetMap.containsKey(exerciseDTO2));
        assertEquals(1, exerciseSetMap.get(exerciseDTO2).size());
        assertEquals(exerciseSet3.getId(), exerciseSetMap.get(exerciseDTO2).get(0).getId());
        assertEquals(exerciseSet3.getWorkout().getId(),  exerciseSetMap.get(exerciseDTO2).get(0).getWorkoutId());
        assertEquals(exerciseSet3.getUser().getId(), exerciseSetMap.get(exerciseDTO2).get(0).getUserId());

        System.out.println(exerciseSetMap);
    }
}