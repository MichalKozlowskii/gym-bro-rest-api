package com.example.gym_bro_rest_api.entities;

import jakarta.persistence.*;
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
    private Exercise exercise;

    @ManyToOne
    private Workout workout;

    @ManyToOne
    private User user;

    private Double weight;

    private Integer reps;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationDate;
}
