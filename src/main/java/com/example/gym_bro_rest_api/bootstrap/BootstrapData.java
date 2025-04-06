package com.example.gym_bro_rest_api.bootstrap;

import com.example.gym_bro_rest_api.entities.*;
import com.example.gym_bro_rest_api.model.SetsReps;
import com.example.gym_bro_rest_api.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseSetRepository exerciseSetRepository;
    private final WorkoutPlanrepository workoutPlanrepository;
    private final WorkoutRepository workoutRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User user;
        Random random = new Random();

        if (userRepository.count() < 1) {
            User user1 = User.builder()
                    .username("user1")
                    .password(passwordEncoder.encode("password"))
                    .enabled(true)
                    .build();
            user = userRepository.save(user1);

            if (exerciseRepository.count() < 10) {
                for (int i = 0; i < 10; i++) {
                    Exercise exercise = Exercise.builder()
                            .name("exercise" + i)
                            .user(user)
                            .build();

                    exerciseRepository.save(exercise);
                }
            }


            // Create workout plan
            if (workoutPlanrepository.count() < 1) {
                workoutPlanrepository.save(WorkoutPlan.builder()
                        .user(user)
                        .name("Workout A")
                        .exercises(new ArrayList<>(List.of(
                                exerciseRepository.findById(1L).get(),
                                exerciseRepository.findById(2L).get(),
                                exerciseRepository.findById(3L).get()
                        )))
                        .setsReps(new ArrayList<>(List.of(
                                SetsReps.builder().sets(3).reps(8).build(),
                                SetsReps.builder().sets(3).reps(8).build(),
                                SetsReps.builder().sets(3).reps(8).build()
                        )))
                        .build());
            }

            if (workoutRepository.count() < 3) {
                WorkoutPlan workoutPlan = workoutPlanrepository.findById(1L).orElseThrow();
                Exercise exercise0 = exerciseRepository.findById(1L).orElseThrow();
                Exercise exercise1 = exerciseRepository.findById(2L).orElseThrow();

                for (int i = 0; i < 6; i++) {
                    int month = 8;
                    int dayOfMonth = 1 + i * 3;

                    Workout workout = Workout.builder()
                            .workoutPlan(workoutPlan)
                            .creationDate(LocalDateTime.of(2024, month, dayOfMonth, 7, 50))
                            .user(user)
                            .build();

                    workoutRepository.save(workout); // Save workout

                    List<ExerciseSet> sets = new ArrayList<>();

                    sets.add(ExerciseSet.builder()
                            .exercise(exercise0)
                            .workout(workout)
                            .user(user)
                            .reps(8)
                            .weight(random.nextDouble(120) + 61)
                            .creationDate(LocalDateTime.of(2024, month, dayOfMonth, 12, 13))
                            .build());

                    sets.add(ExerciseSet.builder()
                            .exercise(exercise0)
                            .workout(workout)
                            .user(user)
                            .reps(10)
                            .weight(random.nextDouble(120) + 61)
                            .creationDate(LocalDateTime.of(2024, month, dayOfMonth, 12, 11))
                            .build());

                    sets.add(ExerciseSet.builder()
                            .exercise(exercise0)
                            .workout(workout)
                            .user(user)
                            .reps(6)
                            .weight(random.nextDouble(120) + 61)
                            .creationDate(LocalDateTime.of(2024, month, dayOfMonth, 12, 0))
                            .build());

                    sets.add(ExerciseSet.builder()
                            .exercise(exercise1)
                            .workout(workout)
                            .user(user)
                            .reps(8)
                            .weight(random.nextDouble(120) + 61)
                            .creationDate(LocalDateTime.of(2024, month, dayOfMonth, 13, 13))
                            .build());

                    sets.add(ExerciseSet.builder()
                            .exercise(exercise1)
                            .workout(workout)
                            .user(user)
                            .reps(10)
                            .weight(random.nextDouble(120) + 61)
                            .creationDate(LocalDateTime.of(2024, month, dayOfMonth, 12, 31))
                            .build());

                    sets.add(ExerciseSet.builder()
                            .exercise(exercise1)
                            .workout(workout)
                            .user(user)
                            .reps(6)
                            .weight(random.nextDouble(120) + 61)
                            .creationDate(LocalDateTime.of(2024, month, dayOfMonth, 8, 0))
                            .build());

                    exerciseSetRepository.saveAll(sets); // Save all sets

                    workout.setSets(sets);
                    workoutRepository.save(workout);
                }
            }
        }
    }
}
