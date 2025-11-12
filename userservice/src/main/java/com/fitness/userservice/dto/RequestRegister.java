package com.fitness.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestRegister {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min =6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "firstname is required")
    @Size(min =2, message = "firstname must be at least 2 characters long")
    private String firstName;

    @NotBlank(message = "lastname is required")
    @Size(min =2, message = "lastname must be at least 2 characters long")
    private String lastName;
}
