package com.example.gym_bro_rest_api.repositories;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {

}
