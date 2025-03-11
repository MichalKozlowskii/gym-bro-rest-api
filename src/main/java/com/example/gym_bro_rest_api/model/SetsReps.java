package com.example.gym_bro_rest_api.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetsReps {
    private Integer sets;
    private Integer reps;
}
