package com.tosin.koins.auth.controller;

import com.tosin.koins.auth.dto.LoginRequest;
import com.tosin.koins.auth.dto.LoginResponse;
import com.tosin.koins.auth.dto.SignupRequest;
import com.tosin.koins.auth.dto.SignupResponse;
import com.tosin.koins.auth.facade.AuthFacade;
import com.tosin.koins.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller.
 *
 * This controller is intentionally thin.
 * It does not contain business logic or response-building logic.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Signup and login APIs")
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new user account and wallet")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return authFacade.signup(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT access token")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return authFacade.login(request);
    }
}