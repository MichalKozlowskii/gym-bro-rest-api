package com.example.gym_bro_rest_api.repositories;

import com.example.gym_bro_rest_api.entities.Workout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {
    Page<Workout> findWorkoutsByUserId(Long userId, Pageable pageable);
}
