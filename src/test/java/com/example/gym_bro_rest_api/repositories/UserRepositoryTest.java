package com.example.gym_bro_rest_api.repositories;

import com.example.gym_bro_rest_api.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void testSaveUser() {
        User newUser = User.builder()
                .username("user1")
                .password("password")
                .build();

        userRepository.save(newUser);
        userRepository.flush();

        assertThat(newUser).isNotNull();
        assertThat(newUser.getId()).isNotNull();
    }

    @Test
    void testFindUserByUsername() {
        User newUser = User.builder()
                .username("user1")
                .password("password")
                .build();

        userRepository.save(newUser);
        userRepository.flush();

        Optional<User> foundUser = userRepository.findByUsername(newUser.getUsername());

        assertThat(foundUser.get()).isNotNull();
        assertThat(foundUser.get().getId()).isNotNull();
    }
}