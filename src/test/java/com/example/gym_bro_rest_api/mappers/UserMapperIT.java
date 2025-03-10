package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.model.UserDTO;
import com.example.gym_bro_rest_api.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserMapperIT {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testUserDtoToUser() {
        UserDTO userDTO = UserDTO.builder()
                .username("user1")
                .password("password")
                .build();

        User user = userMapper.UserDtoToUser(userDTO);

        assertThat(user.getUsername()).isEqualTo("user1");
        assertThat(user.getPassword()).isNotEqualTo("password");
        assertThat(passwordEncoder.matches("password", user.getPassword())).isTrue();
    }

    @Test
    void testUserToUserDTO() {
        User user = User.builder()
                .username("user1")
                .password("encryptedPassword")
                .build();

        // Act
        UserDTO userDTO = userMapper.UserToUserDto(user);

        // Assert
        assertThat(userDTO.getUsername()).isEqualTo("user1");
        assertThat(userDTO.getPassword()).isEqualTo("encryptedPassword");
    }
}