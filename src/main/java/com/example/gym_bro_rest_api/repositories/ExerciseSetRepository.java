package com.example.gym_bro_rest_api.repositories;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExerciseSetRepository extends JpaRepository<ExerciseSet, Long> {
    List<ExerciseSet> findExerciseSetsByExerciseId(Long exerciseId);
    Optional<ExerciseSet> findTopByExerciseIdOrderByRepsDesc(Long exerciseId);
    Optional<ExerciseSet> findTopByExerciseIdOrderByRepsAsc(Long exerciseId);
    Optional<ExerciseSet> findTopByExerciseIdOrderByWeightDesc(Long exerciseId);
    Optional<ExerciseSet> findTopByExerciseIdOrderByWeightAsc(Long exerciseId);
    Optional<ExerciseSet> findTopByExerciseIdOrderByCreationDateDesc(Long exerciseId);
    Optional<ExerciseSet> findTopByExerciseIdOrderByCreationDateAsc(Long exerciseId);
    @Query("""
    SELECT s FROM ExerciseSet s
    WHERE s.exercise.id = :exerciseId
    AND s.weight = (SELECT MAX(s2.weight) FROM ExerciseSet s2 WHERE s2.exercise.id = :exerciseId AND s2.creationDate = s.creationDate)
    GROUP BY s.creationDate
    ORDER BY s.creationDate ASC
    """)
    List<ExerciseSet> findBestSetPerDayByExerciseId(@Param("exerciseId") Long exerciseId);
}
