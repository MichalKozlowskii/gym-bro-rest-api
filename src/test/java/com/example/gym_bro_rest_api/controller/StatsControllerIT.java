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
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

// all test entities saved in BootstrapData
@SpringBootTest
@ActiveProfiles("localmysql")
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

        // test leastWeight, mostWeight

        // sort descending by weight
        sets.sort(Comparator.comparingDouble(ExerciseSet::getWeight).reversed());

        assertThat(sets.getFirst().getId()).isEqualTo(statsView.getMostWeightSet().getId());
        assertThat(sets.getLast().getId()).isEqualTo(statsView.getLeastWeightSet().getId());

        // test leastReps, mostReps

        // sort descending by reps
        sets.sort(Comparator.comparingInt(ExerciseSet::getReps).reversed());

        assertThat(sets.getFirst().getReps()).isEqualTo(statsView.getMostRepsSet().getReps());
        assertThat(sets.getLast().getReps()).isEqualTo(statsView.getLeastRepsSet().getReps());

        // test newestSet, oldestSet, plotData

        // sort ascending by date
        sets.sort(Comparator.comparing(ExerciseSet::getCreationDate));

        // In test data, there are always 3 sets per exercise, in
        for (int i = 0; i < 6; i++) {
            ExerciseSet set1 = sets.get(i * 3);
            ExerciseSet set2 = sets.get(i * 3 + 1);
            ExerciseSet set3 = sets.get(i * 3 + 2);

            Double mostWeight = Math.max(set1.getWeight(), set2.getWeight());
            mostWeight = Math.max(mostWeight, set3.getWeight());

            assertThat(statsView.getPlotData().get(i).getWeight()).isEqualTo(mostWeight);
        }

        assertThat(sets.getFirst().getId()).isEqualTo(statsView.getNewestSet().getId());
        assertThat(sets.getLast().getId()).isEqualTo(statsView.getOldestSet().getId());
    }
}