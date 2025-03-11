package com.example.gym_bro_rest_api.repositories;

import com.example.gym_bro_rest_api.entities.WorkoutPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutPlanrepository extends JpaRepository<WorkoutPlan, Long> {
    Page<WorkoutPlan> findWorkoutPlansByUserId(Long id, Pageable pageable);
    Boolean existsByIdAndUserId(Long id, Long userId);
}
