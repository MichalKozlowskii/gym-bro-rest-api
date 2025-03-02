package com.example.gym_bro_rest_api.mappers;

import com.example.gym_bro_rest_api.dto.UserDTO;
import com.example.gym_bro_rest_api.entities.User;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class UserMapper {
    protected PasswordEncoder passwordEncoder;

    @Mapping(target = "password", ignore = true) // Ignore password during initial mapping
    public abstract User UserDtoToUser(UserDTO userDTO);

    @AfterMapping
    protected void encryptPassword(UserDTO userDTO, @MappingTarget User user) {
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    }

    public abstract UserDTO UserToUserDto(User user);
}
