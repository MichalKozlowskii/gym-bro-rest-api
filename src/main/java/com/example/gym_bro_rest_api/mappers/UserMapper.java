package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.model.UserDTO;
import com.example.gym_bro_rest_api.entities.User;

public interface UserMapper {
    User UserDtoToUser(UserDTO userDTO);

    UserDTO UserToUserDto(User user);
}
