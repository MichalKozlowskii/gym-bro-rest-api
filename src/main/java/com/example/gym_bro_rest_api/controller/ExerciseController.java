package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.ExerciseDTO;
import com.example.gym_bro_rest_api.services.ExerciseService;
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
    ResponseEntity<Map<String, String>> createNewExercise(@RequestBody ExerciseDTO exerciseDTO,
                                                          @AuthenticationPrincipal User user) {
        if (exerciseDTO.getName().isBlank() || exerciseDTO.getName().isEmpty()) {
           throw new BadNameException();
        }

        Long exerciseId = exerciseService.saveNewExercise(exerciseDTO, user).getId();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/api/exercise/" + exerciseId)
                .body(Map.of("success", "Exercise created."));
    }

    @GetMapping("/{exerciseId}")
    ExerciseDTO getExerciseById(@PathVariable("exerciseId") Long id, @AuthenticationPrincipal User user) {

        ExerciseDTO exerciseDTO = exerciseService.getExerciseById(id).orElseThrow(NotFoundException::new);

        if (!Objects.equals(user.getId(), exerciseDTO.getUserId())) {
            throw new NoAccessException();
        }

        return exerciseDTO;
    }

    @PutMapping("/{exerciseId}")
    ResponseEntity<Map<String, String>> updateExerciseById(@PathVariable("exerciseId") Long id,
                                                           @RequestBody ExerciseDTO exerciseDTO,
                                                           @AuthenticationPrincipal User user) {
        if (exerciseDTO.getName().isBlank() || exerciseDTO.getName().isEmpty()) {
            throw new BadNameException();
        }

        ExerciseDTO updatedDTO = exerciseService.updateExerciseById(id, exerciseDTO, user)
                .orElseThrow(NotFoundException::new);
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Exercise updated."));
    }

    @DeleteMapping("/{exerciseId}")
    ResponseEntity<Map<String, String>> deleteExerciseById(@PathVariable("exerciseId") Long id,
                                                           @AuthenticationPrincipal User user) {
        exerciseService.deleteExerciseById(id, user.getId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("success", "Exercise deleted."));
    }

    @GetMapping()
    Page<ExerciseDTO> listExercisesOfUser(@AuthenticationPrincipal User user,
                                          @RequestParam(required = false) Integer pageNumber,
                                          @RequestParam(required = false) Integer pageSize) {

        return exerciseService.listExercisesOfUser(user.getId(), pageNumber, pageSize);
    }
}
