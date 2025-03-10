package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.services.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
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
    ResponseEntity<Map<String, String>> createNewWorkoutPlan(@RequestBody WorkoutPlanDTO workoutPlanDTO,
                                                             @AuthenticationPrincipal User user) {
        if (workoutPlanDTO.getName().isEmpty() || workoutPlanDTO.getName().isBlank()) {
            throw new BadNameException();
        }

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
                                                              @RequestBody WorkoutPlanDTO workoutPlanDTO,
                                                              @AuthenticationPrincipal User user) {
        if (workoutPlanDTO.getName().isEmpty() || workoutPlanDTO.getName().isBlank()) {
            throw new BadNameException();
        }

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
}
