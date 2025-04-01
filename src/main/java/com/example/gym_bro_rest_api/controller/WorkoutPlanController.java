package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.services.workoutplan.WorkoutPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workout-plan")
public class WorkoutPlanController {
    private final WorkoutPlanService workoutPlanService;

    @PostMapping("/create")
    ResponseEntity<Map<String, String>> createNewWorkoutPlan(@Valid @RequestBody WorkoutPlanDTO workoutPlanDTO,
                                                             @AuthenticationPrincipal User user) {

        Long workoutPlanId = workoutPlanService.saveNewWorkoutPlan(workoutPlanDTO, user).getId();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/workout-plan/" + workoutPlanId)
                .body(Map.of("success", "Workout plan created."));
    }

    @GetMapping("/{workoutPlanId}")
    WorkoutPlanDTO getWorkoutPlanById(@PathVariable("workoutPlanId") Long id, @AuthenticationPrincipal User user) {
        WorkoutPlanDTO workoutPlanDTO = workoutPlanService.getWorkoutPlanById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(user.getId(), workoutPlanDTO.getUserId())) {
            throw new NoAccessException();
        }

        return workoutPlanDTO ;
    }

    @PutMapping("/{workoutPlanId}")
    ResponseEntity<Map<String, String>> updateWorkoutPlanById(@PathVariable("workoutPlanId") Long id,
                                                              @Valid @RequestBody WorkoutPlanDTO workoutPlanDTO,
                                                              @AuthenticationPrincipal User user) {
        workoutPlanService.updateWorkoutPlanById(id, workoutPlanDTO, user).orElseThrow(NotFoundException::new);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Workout plan updated."));
    }

    @DeleteMapping("/{workoutPlanId}")
    ResponseEntity<Map<String, String>> deleteWorkoutPlanById(@PathVariable("workoutPlanId") Long id,
                                                              @AuthenticationPrincipal User user) {
        workoutPlanService.deleteWorkoutPlanById(id, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Workout plan deleted."));
    }

    @GetMapping()
    Page<WorkoutPlanDTO> listWorkoutPlansOfUser(@AuthenticationPrincipal User user,
                                                @RequestParam(required = false) Integer pageNumber,
                                                @RequestParam(required = false) Integer pageSize) {

        return workoutPlanService.listExercisesOfUser(user, pageNumber, pageSize);
    }
}
