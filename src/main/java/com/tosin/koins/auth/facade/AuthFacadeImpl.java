package com.tosin.koins.auth.facade;

import com.tosin.koins.auth.dto.*;
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

    @Override
    public ApiResponse<OtpResponse> forgotPassword(ForgotPasswordRequest request) {
        OtpResponse response = authService.forgotPassword(request);
        return responseFactory.success("OTP sent successfully", response);
    }

    @Override
    public ApiResponse<OtpResponse> resendOtp(ResendOtpRequest request) {
        OtpResponse response = authService.resendOtp(request);
        return responseFactory.success("OTP resent successfully", response);
    }

    @Override
    public ApiResponse<ResetPasswordResponse> resetPassword(ResetPasswordRequest request) {
        ResetPasswordResponse response = authService.resetPassword(request);
        return responseFactory.success("Password reset successful", response);
    }

    @Override
    public ApiResponse<Void> logout() {
        return responseFactory.success("Logout successful. Please discard the token on the client side.");
    }
}