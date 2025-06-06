package com.example.gym_bro_rest_api.controller;

import com.example.gym_bro_rest_api.controller.exceptions.NoAccessException;
import com.example.gym_bro_rest_api.controller.exceptions.NotFoundException;
import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.ExerciseSet;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.StatsView;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.ExerciseSetRepository;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    User user;
    Exercise exerciseWithSets;
    Exercise exerciseNoSets;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        user = userRepository.findById(1L).get();
        exerciseWithSets = exerciseRepository.findById(1L).get();
        exerciseNoSets = exerciseRepository.findById(4L).get();

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testGetExerciseStatsById_Web_NoContent() throws Exception {
        mockMvc.perform(get("/api/stats/{id}", exerciseNoSets.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetExerciseStatsById_Web_Success() throws Exception {
        mockMvc.perform(get("/api/stats/{id}", exerciseWithSets.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mostWeightSet").isNotEmpty())
                .andExpect(jsonPath("$.leastWeightSet").isNotEmpty())
                .andExpect(jsonPath("$.mostRepsSet").isNotEmpty())
                .andExpect(jsonPath("$.leastRepsSet").isNotEmpty())
                .andExpect(jsonPath("$.newestSet").isNotEmpty())
                .andExpect(jsonPath("$.oldestSet").isNotEmpty())
                .andExpect(jsonPath("$.plotData").isNotEmpty());
    }

    @Test
    void testGetExerciseStatsById_NoContent() {
        ResponseEntity<StatsView> response = statsController.getExerciseStatsById(exerciseNoSets.getId(), user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testGetExerciseStatsById_NotFound() {
        assertThrows(NotFoundException.class, () ->
                statsController.getExerciseStatsById(11111111111111L, user));
    }

    @Test
    @Transactional
    @Rollback
    void testGetExerciseStatsById_NoAccess() {
        User anotherUser = userRepository.save(User.builder()
                        .username("user11111")
                        .password("password11")
                .build());

        assertThrows(NoAccessException.class, () ->
                statsController.getExerciseStatsById(exerciseWithSets.getId(), anotherUser));
    }

    @Test
    void testGetExerciseStatsById_Success() {
        ResponseEntity<StatsView> response = statsController.getExerciseStatsById(exerciseWithSets.getId(), user);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        StatsView statsView = response.getBody();

        List<ExerciseSet> sets = exerciseSetRepository.findExerciseSetsByExerciseId(exerciseWithSets.getId());

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

        assertThat(sets.getFirst().getId()).isEqualTo(statsView.getOldestSet().getId());
        assertThat(sets.getLast().getId()).isEqualTo(statsView.getNewestSet().getId());
    }
}