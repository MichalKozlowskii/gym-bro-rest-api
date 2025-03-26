package com.example.gym_bro_rest_api.repositories;

import com.example.gym_bro_rest_api.entities.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Page<Exercise> findExercisesByUserId(Long id, Pageable pageable);
}
