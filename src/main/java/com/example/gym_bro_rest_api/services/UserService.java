package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.dto.UserDTO;
import com.example.gym_bro_rest_api.entities.User;

public interface UserService {
    UserDTO saveNewUser(UserDTO userDTO);
}
