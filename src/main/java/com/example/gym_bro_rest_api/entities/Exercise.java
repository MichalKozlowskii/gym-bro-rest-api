package com.example.gym_bro_rest_api.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "exercises")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name must not be blank.")
    @NotNull(message = "Name must not be null.")
    private String name;

    private String demonstrationUrl;

    @ManyToOne
    @NotNull(message = "User must not be null.")
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime creationDate;
}
