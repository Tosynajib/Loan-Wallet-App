package com.tosin.koins.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for user signup.
 *
 * Validation annotations protect the service layer from bad input.
 */
public record SignupRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9+]{10,15}$", message = "Phone number must be valid")
        String phoneNumber,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "BVN or NIN is required")
        @Size(min = 10, max = 15, message = "BVN/NIN must be between 10 and 15 characters")
        String bvnOrNin
) {
}