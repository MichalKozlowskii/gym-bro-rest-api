package com.example.gym_bro_rest_api.entities;

import com.example.gym_bro_rest_api.model.SetsReps;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workout_plans")
public class WorkoutPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    @ManyToMany
    private List<Exercise> exercises = new ArrayList<>();

    @ElementCollection
    private List<SetsReps> setsReps = new ArrayList<>();

    @ManyToOne
    @NotNull
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationDate;
}
