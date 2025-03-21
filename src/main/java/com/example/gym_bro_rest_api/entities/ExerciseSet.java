package com.example.gym_bro_rest_api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sets")
public class ExerciseSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Exercise exercise;

    @ManyToOne
    @NotNull
    private Workout workout;

    @ManyToOne
    @NotNull
    private User user;

    @NotNull
    private Double weight;

    @NotNull
    private Integer reps;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationDate;
}
