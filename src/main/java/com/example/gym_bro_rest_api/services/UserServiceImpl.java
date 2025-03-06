package com.example.gym_bro_rest_api.services;

import com.example.gym_bro_rest_api.entities.User;
import com.example.gym_bro_rest_api.model.UserDTO;
import com.example.gym_bro_rest_api.mappers.UserMapper;
import com.example.gym_bro_rest_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserDTO saveNewUser(UserDTO userDTO) {
        return userMapper.UserToUserDto(userRepository.save(userMapper.UserDtoToUser(userDTO)));
    }

    @Override
    public Optional<UserDTO> findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::UserToUserDto);
    }
}
