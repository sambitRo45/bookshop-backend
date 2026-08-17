package com.bookshop.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @Email(message = "Email is required")
    @NotBlank
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

}