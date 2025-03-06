package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.mappers.UserMapper;
import com.example.gym_bro_rest_api.model.UserDTO;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = User.builder().username("testUser").build();

        userDTO = UserDTO.builder().username("testUser").build();
    }

    @Test
    void findUserByUsername_UserExists_ReturnsUserDTO() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(userMapper.UserToUserDto(user)).thenReturn(userDTO);

        Optional<UserDTO> result = userService.findUserByUsername("testUser");

        assertTrue(result.isPresent());
        assertEquals("testUser", result.get().getUsername());

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userMapper, times(1)).UserToUserDto(user);
    }

    @Test
    void findUserByUsername_UserNotFound_ReturnsEmptyOptional() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        Optional<UserDTO> result = userService.findUserByUsername("testUser");

        assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findByUsername("testUser");
        verify(userMapper, never()).UserToUserDto(any());
    }
}