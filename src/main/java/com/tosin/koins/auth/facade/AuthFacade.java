package com.tosin.koins.auth.facade;

import com.tosin.koins.auth.dto.LoginRequest;
import com.tosin.koins.auth.dto.LoginResponse;
import com.tosin.koins.auth.dto.SignupRequest;
import com.tosin.koins.auth.dto.SignupResponse;
import com.tosin.koins.common.response.ApiResponse;

/**
 * Facade for authentication use cases.
 *
 * The facade coordinates between:
 * - Controller
 * - Service
 * - Response factory
 */
public interface AuthFacade {

    ApiResponse<SignupResponse> signup(SignupRequest request);

    ApiResponse<LoginResponse> login(LoginRequest request);
}