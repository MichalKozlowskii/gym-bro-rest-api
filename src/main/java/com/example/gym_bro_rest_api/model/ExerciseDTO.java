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

    @NotNull(message = "Name must not be null.")
    @NotBlank(message = "Name must not be blank.")
    private String name;

    private String demonstrationUrl;
    private Long userId;

    private LocalDateTime creationDate;
}
