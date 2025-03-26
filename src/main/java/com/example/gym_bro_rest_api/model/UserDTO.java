package com.example.gym_bro_rest_api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDTO {
    private Long id;

    @NotNull(message = "Username must not be null.")
    @NotBlank(message = "Username must not be blank.")
    private String username;

    @NotNull(message = "Password must not be null.")
    @NotBlank(message = "Password must not be blank.")
    @Size(min = 7, message = "Password must be at least 7 characters long")
    private String password;
}
