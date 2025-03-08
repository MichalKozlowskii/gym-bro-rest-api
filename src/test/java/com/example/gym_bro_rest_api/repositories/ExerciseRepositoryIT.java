package com.example.gym_bro_rest_api.repositories;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExerciseRepositoryIT {

    @Autowired
    ExerciseRepository exerciseRepository;

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .username("testuser")
                .password("testpassword")
                .enabled(true)
                .build());

        exerciseRepository.save(Exercise.builder()
                        .name("exc1")
                        .demonstrationUrl("adsad")
                        .user(user)
                .build());

        exerciseRepository.save(Exercise.builder()
                .name("exc2")
                .demonstrationUrl("adsad")
                .user(user)
                .build());

        exerciseRepository.save(Exercise.builder()
                .name("exc3")
                .demonstrationUrl("adsad")
                .user(user)
                .build());
    }

    @Test
    void testFindExercisesByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Exercise> exercisePage = exerciseRepository.findExercisesByUserId(user.getId(), pageable);

        List<Exercise> exerciseList = exercisePage.getContent();

        assertThat(exerciseList.size()).isEqualTo(3);
    }
}