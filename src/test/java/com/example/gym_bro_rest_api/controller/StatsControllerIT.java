package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.StatsView;
import com.example.gym_bro_rest_api.repositories.ExerciseSetRepository;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

// all test entities saved in BootstrapData
@SpringBootTest
class StatsControllerIT {
    @Autowired
    StatsController statsController;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ExerciseSetRepository exerciseSetRepository;

    User user;

    @BeforeEach
    void setup() {
        user = userRepository.findById(1L).get();
    }

    @Test
    void testGetExerciseStatsById_Success() {
        ResponseEntity<StatsView> response = statsController.getExerciseStatsById(1L, user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        StatsView statsView = response.getBody();

        List<ExerciseSet> sets = exerciseSetRepository.findExerciseSetsByExerciseId(1L);

        // test leastWeight, mostWeight, plotData

        // sort descending by weight
        sets.sort(Comparator.comparingDouble(ExerciseSet::getWeight).reversed());

        assertThat(sets.getFirst().getId()).isEqualTo(statsView.getMostWeightSet().getId());
        assertThat(sets.getLast().getId()).isEqualTo(statsView.getLeastWeightSet().getId());

        System.out.println(statsView.getPlotData().stream()
                .map(exerciseSetDTO ->
                    Map.of(exerciseSetDTO.getWeight(), exerciseSetDTO.getCreationDate())
                )
                .collect(Collectors.toList()));

        System.out.println(sets.stream()
                .map(exerciseSet ->
                        Map.of(exerciseSet.getWeight(), exerciseSet.getCreationDate())
                )
                .collect(Collectors.toList()));

        for (int i = 0; i < statsView.getPlotData().size(); i++) {
            assertThat(statsView.getPlotData().get(i).getWeight()).isEqualTo(sets.get(i * 3).getWeight());
        }

        // test leastReps, mostReps

        // sort descending by reps
        sets.sort(Comparator.comparingInt(ExerciseSet::getReps).reversed());

        assertThat(sets.getFirst().getId()).isEqualTo(statsView.getMostRepsSet().getId());
        assertThat(sets.getLast().getId()).isEqualTo(statsView.getLeastRepsSet().getId());

        // test newestSet, oldestSet

        // sort ascending by date
        sets.sort(Comparator.comparing(ExerciseSet::getCreationDate));

        assertThat(sets.getFirst().getId()).isEqualTo(statsView.getNewestSet().getId());
        assertThat(sets.getLast().getId()).isEqualTo(statsView.getOldestSet().getId());
    }
}