package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.dto.UserDTO;
import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.mappers.UserMapper;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    @Override
    public UserDTO saveNewUser(UserDTO userDTO) {
        return userMapper.UserToUserDto(userRepository.save(userMapper.UserDtoToUser(userDTO)));
    }
}
