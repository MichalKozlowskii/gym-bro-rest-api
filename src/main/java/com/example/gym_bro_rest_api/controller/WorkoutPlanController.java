package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.WorkoutPlanDTO;
import com.example.gym_bro_rest_api.services.WorkoutPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
}
