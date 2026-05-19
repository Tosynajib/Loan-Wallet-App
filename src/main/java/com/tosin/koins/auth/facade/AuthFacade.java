package com.tosin.koins.auth.facade;

import com.tosin.koins.auth.dto.*;
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

    ApiResponse<OtpResponse> forgotPassword(ForgotPasswordRequest request);

    ApiResponse<OtpResponse> resendOtp(ResendOtpRequest request);

    ApiResponse<ResetPasswordResponse> resetPassword(ResetPasswordRequest request);

    ApiResponse<Void> logout();
}