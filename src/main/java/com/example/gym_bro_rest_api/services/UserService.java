package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.model.UserDTO;

public interface UserService {
    UserDTO saveNewUser(UserDTO userDTO);
}
