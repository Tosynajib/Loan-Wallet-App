package com.tosin.koins.auth.service;

import com.tosin.koins.auth.dto.LoginRequest;
import com.tosin.koins.auth.dto.LoginResponse;
import com.tosin.koins.auth.dto.SignupRequest;
import com.tosin.koins.auth.dto.SignupResponse;

public interface AuthService {

    SignupResponse signup(SignupRequest request);

    LoginResponse login(LoginRequest request);
}
