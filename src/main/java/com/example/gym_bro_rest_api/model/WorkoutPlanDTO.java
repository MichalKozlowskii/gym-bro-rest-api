package com.example.gym_bro_rest_api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
public class WorkoutPlanDTO {
    private Long id;
    @NotBlank(message = "Name must not be blank.")
    @NotNull(message = "Name must not be null.")
    private String name;
    private Long userId;
    private List<ExerciseDTO> exercises;
    private List<SetsReps> setsReps;
    private LocalDateTime creationDate;
}
