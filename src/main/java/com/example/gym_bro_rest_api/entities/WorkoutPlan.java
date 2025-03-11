package com.example.gym_bro_rest_api.entities;

import com.example.gym_bro_rest_api.model.SetsReps;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
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
    private List<Exercise> exercises;

    @ElementCollection
    private List<SetsReps> setsReps;

    @ManyToOne
    @NotNull
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate creationDate;
}
