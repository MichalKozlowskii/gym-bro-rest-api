package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseSetDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutCreationDTO;
import com.example.gym_bro_rest_api.model.workout.WorkoutViewDTO;
import com.example.gym_bro_rest_api.services.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workout")
public class WorkoutController {
    private final WorkoutService workoutService;

    @PostMapping("/create")
    ResponseEntity<Map<String, String>> createNewWorkout(@Valid @RequestBody WorkoutCreationDTO workoutCreationDTO,
                                                         @AuthenticationPrincipal User user) {

        Long createdWorkoutId = workoutService.saveNewWorkout(workoutCreationDTO, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/workout/" + createdWorkoutId)
                .body(Map.of("success", "Workout created."));
    }

    @PostMapping("/{workoutId}/addset")
    ResponseEntity<Map<String, String>> addNewSet(@Valid @RequestBody ExerciseSetDTO exerciseSetDTO,
                                                  @PathVariable("workoutId") Long workoutId,
                                                  @AuthenticationPrincipal User user) {

        workoutService.addNewSet(workoutId, exerciseSetDTO, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/workout/" + workoutId)
                .body(Map.of("success", "Set created and added to workout."));
    }

    @DeleteMapping("/{workoutId}/deleteset/{setId}")
    ResponseEntity<Map<String, String>> deleteSet(@PathVariable("workoutId") Long workoutId,
                                                  @PathVariable("setId") Long setId,
                                                  @AuthenticationPrincipal User user) {

        Boolean isDeleted = workoutService.deleteSet(workoutId, setId, user);

        if (!isDeleted) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("failure", "Set doesn't belong to this workout."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Set deleted."));
    }

    @DeleteMapping("/{workoutId}")
    ResponseEntity<Map<String, String>> deleteWorkout(@PathVariable("workoutId") Long workoutId,
                                                      @AuthenticationPrincipal User user) {
        workoutService.deleteWorkoutById(workoutId, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Workout deleted."));
    }

    @GetMapping("/{workoutId}")
    WorkoutViewDTO getWorkoutById(@PathVariable("workoutId") Long workoutId,
                                  @AuthenticationPrincipal User user) {

        return workoutService.getWorkoutById(workoutId, user);
    }

    @GetMapping()
    Page<WorkoutViewDTO> listWorkouts(@AuthenticationPrincipal User user,
                                      @RequestParam(required = false) Integer pageNumber,
                                      @RequestParam(required = false) Integer pageSize) {

        return workoutService.listWorkoutsOfUser(user, pageNumber, pageSize);
    }
}
