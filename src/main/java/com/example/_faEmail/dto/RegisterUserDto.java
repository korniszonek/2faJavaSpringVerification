package com.example._faEmail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public record RegisterUserDto(
        @NotBlank String nickname,
        @Email(message = "Incorrect email format") String email,
        @NotBlank String password,
        @NotBlank String repeatedPassword
) {
    public RegisterUserDto {
        Objects.requireNonNull(nickname);
        Objects.requireNonNull(email);
        Objects.requireNonNull(password);
        Objects.requireNonNull(repeatedPassword);
    }
}
