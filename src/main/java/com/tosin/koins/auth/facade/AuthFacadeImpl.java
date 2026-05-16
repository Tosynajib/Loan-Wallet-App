package com.tosin.koins.auth.facade;

import com.tosin.koins.auth.dto.LoginRequest;
import com.tosin.koins.auth.dto.LoginResponse;
import com.tosin.koins.auth.dto.SignupRequest;
import com.tosin.koins.auth.dto.SignupResponse;
import com.tosin.koins.auth.service.AuthService;
import com.tosin.koins.common.response.ApiResponse;
import com.tosin.koins.common.response.ApplicationResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Authentication facade implementation.
 *
 * Why this exists:
 * - Keeps the controller plain.
 * - Keeps AuthService focused on business logic.
 * - Handles API response wrapping in one place.
 */
@Component
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final AuthService authService;
    private final ApplicationResponseFactory responseFactory;

    @Override
    public ApiResponse<SignupResponse> signup(SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return responseFactory.success("Signup successful", response);
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        LoginResponse response = authService.login(request);
        return responseFactory.success("Login successful", response);
    }
}