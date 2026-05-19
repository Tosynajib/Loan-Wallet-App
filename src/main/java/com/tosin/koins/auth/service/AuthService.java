package com.tosin.koins.auth.service;

import com.tosin.koins.auth.dto.*;

public interface AuthService {

    SignupResponse signup(SignupRequest request);

    LoginResponse login(LoginRequest request);

    OtpResponse forgotPassword(ForgotPasswordRequest request);

    OtpResponse resendOtp(ResendOtpRequest request);

    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}
