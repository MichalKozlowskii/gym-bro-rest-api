package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.services.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exercise")
@Slf4j
public class ExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping("/create")
    ResponseEntity<Map<String, String>> createNewExercise(@Valid @RequestBody ExerciseDTO exerciseDTO,
                                                          @AuthenticationPrincipal User user) {

        Long exerciseId = exerciseService.saveNewExercise(exerciseDTO, user).getId();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/exercise/" + exerciseId)
                .body(Map.of("success", "Exercise created."));
    }

    @GetMapping("/{exerciseId}")
    ExerciseDTO getExerciseById(@PathVariable("exerciseId") Long id, @AuthenticationPrincipal User user) {
        return exerciseService.getExerciseById(id, user);
    }

    @PutMapping("/{exerciseId}")
    ResponseEntity<Map<String, String>> updateExerciseById(@PathVariable("exerciseId") Long id,
                                                           @Valid @RequestBody ExerciseDTO exerciseDTO,
                                                           @AuthenticationPrincipal User user) {
        exerciseService.updateExerciseById(id, exerciseDTO, user);
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Exercise updated."));
    }

    @DeleteMapping("/{exerciseId}")
    ResponseEntity<Map<String, String>> deleteExerciseById(@PathVariable("exerciseId") Long id,
                                                           @AuthenticationPrincipal User user) {
        exerciseService.deleteExerciseById(id, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Exercise deleted."));
    }

    @GetMapping()
    Page<ExerciseDTO> listExercisesOfUser(@AuthenticationPrincipal User user,
                                          @RequestParam(required = false) Integer pageNumber,
                                          @RequestParam(required = false) Integer pageSize) {

        return exerciseService.listExercisesOfUser(user, pageNumber, pageSize);
    }
}
