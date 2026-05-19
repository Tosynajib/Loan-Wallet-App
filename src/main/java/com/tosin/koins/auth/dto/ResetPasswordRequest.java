package com.tosin.koins.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(@NotBlank(message = "Email is required")
                                   @Email(message = "Email must be valid")
                                   String email,

                                   @NotBlank(message = "OTP is required")
                                   @Size(min = 6, max = 6, message = "OTP must be 6 digits")
                                   String otp,

                                   @NotBlank(message = "New password is required")
                                   @Size(min = 8, message = "New password must be at least 8 characters")
                                   String newPassword) {
}
