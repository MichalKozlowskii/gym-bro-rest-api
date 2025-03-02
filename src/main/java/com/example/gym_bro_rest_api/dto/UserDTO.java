package com.example.gym_bro_rest_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {
    private Long id;

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    @Size(min = 7)
    private String password;
}
