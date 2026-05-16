package com.tosin.koins.auth.service;

import com.tosin.koins.auth.dto.LoginRequest;
import com.tosin.koins.auth.dto.LoginResponse;
import com.tosin.koins.auth.dto.SignupRequest;
import com.tosin.koins.auth.dto.SignupResponse;
import com.tosin.koins.common.enums.AccountStatus;
import com.tosin.koins.common.enums.UserRole;
import com.tosin.koins.common.enums.WalletStatus;
import com.tosin.koins.common.exception.ConflictException;
import com.tosin.koins.common.security.JwtService;
import com.tosin.koins.user.entity.User;
import com.tosin.koins.user.repository.UserRepository;
import com.tosin.koins.wallet.entity.Wallet;
import com.tosin.koins.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Authentication business logic implementation.
 *
 * This class contains business rules only.
 * It does not build HTTP responses.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Signup creates both the user and the wallet.
     *
     * @Transactional means:
     * - If user is saved but wallet creation fails, the user save is rolled back.
     * - This keeps the database consistent.
     */
    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String email = request.email().trim().toLowerCase();
        String phoneNumber = request.phoneNumber().trim();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email address already exists");
        }

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException("Phone number already exists");
        }

        User user = User.builder()
                .fullName(request.fullName().trim())
                .email(email)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(request.password()))
                .bvnOrNin(request.bvnOrNin().trim())
                .status(AccountStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        User savedUser = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .userId(savedUser.getId())
                .balance(BigDecimal.ZERO)
                .currency("NGN")
                .status(WalletStatus.ACTIVE)
                .build();

        Wallet savedWallet = walletRepository.save(wallet);

        return new SignupResponse(
                savedUser.getId(),
                savedWallet.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                "Signup successful. Wallet created automatically."
        );
    }

    /**
     * Login uses Spring Security's AuthenticationManager to validate credentials.
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ConflictException("Invalid login credentials"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                token,
                "Bearer",
                jwtService.getJwtExpirationMinutes()
        );
    }
}