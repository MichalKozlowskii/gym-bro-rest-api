package com.example.gym_bro_rest_api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
public class ExerciseDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    private String demonstrationUrl;

    @NotNull
    private Long userId;

    private LocalDateTime creationDate;
}
