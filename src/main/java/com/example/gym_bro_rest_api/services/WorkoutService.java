package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.entities.Workout;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutViewDTO;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

public interface WorkoutService {
    Long saveNewWorkout(WorkoutCreationDTO workoutCreationDTO, User user);
    Workout addNewSet(Long workoutId, ExerciseSetDTO exerciseSetDTO, User user) throws BadRequestException;
    Boolean deleteSet(Long workoutId, Long setId, User user);
    void deleteWorkoutById(Long workoutId, User user);
    WorkoutViewDTO getWorkoutById(Long workoutId, User user);
    Page<WorkoutViewDTO> listWorkoutsOfUser(User user, Integer pageNumber, Integer pageSize);
}
