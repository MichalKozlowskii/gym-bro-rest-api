package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.model.UserDTO;

import java.util.Optional;

public interface UserService {
    UserDTO saveNewUser(UserDTO userDTO);
    Optional<UserDTO> findUserByUsername(String username);
}
