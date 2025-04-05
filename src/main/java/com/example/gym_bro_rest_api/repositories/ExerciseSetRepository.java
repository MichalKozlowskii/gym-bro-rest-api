package com.example.gym_bro_rest_api.repositories;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {
    ExerciseSet findTopByExerciseIdOrderByRepsDesc(Long exerciseId);
    ExerciseSet findTopByExerciseIdOrderByRepsAsc(Long exerciseId);
    ExerciseSet findTopByExerciseIdOrderByWeightDesc(Long exerciseId);
    ExerciseSet findTopByExerciseIdOrderByWeightAsc(Long exerciseId);
    ExerciseSet findTopByExerciseIdOrderByCreationDateDesc(Long exerciseId);
    ExerciseSet findTopByExerciseIdOrderByCreationDateAsc(Long exerciseId);
    @Query("""
    SELECT s FROM ExerciseSet s
    WHERE s.exercise.id = :exerciseId
    AND s.weight = (SELECT MAX(s2.weight) FROM ExerciseSet s2 WHERE s2.exercise.id = :exerciseId AND s2.creationDate = s.creationDate)
    GROUP BY s.creationDate
    ORDER BY s.creationDate ASC
    """)
    List<ExerciseSet> findBestSetPerDay(@Param("exerciseId") Long exerciseId);
}
