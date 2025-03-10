package com.example.gym_bro_rest_api.bootstrap;

import com.example.gym_bro_rest_api.entities.Exercise;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.repositories.ExerciseRepository;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import com.example.gym_bro_rest_api.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User user;

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
        }
    }
}
