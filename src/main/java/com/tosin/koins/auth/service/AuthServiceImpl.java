package com.tosin.koins.auth.service;

import com.tosin.koins.auth.dto.*;
import com.tosin.koins.auth.entity.PasswordOtp;
import com.tosin.koins.auth.repository.PasswordOtpRepository;
import com.tosin.koins.common.enums.AccountStatus;
import com.tosin.koins.common.enums.OtpPurpose;
import com.tosin.koins.common.enums.UserRole;
import com.tosin.koins.common.enums.WalletStatus;
import com.tosin.koins.common.exception.BadRequestException;
import com.tosin.koins.common.exception.ConflictException;
import com.tosin.koins.common.exception.NotFoundException;
import com.tosin.koins.common.security.JwtService;
import com.tosin.koins.common.util.OtpGenerator;
import com.tosin.koins.integration.sms.SmsProvider;
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
import java.time.LocalDateTime;
import java.util.List;

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
    private final PasswordOtpRepository passwordOtpRepository;
    private final SmsProvider smsProvider;

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

    @Override
    @Transactional
    public OtpResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return generateAndSendPasswordResetOtp(user);
    }

    @Override
    @Transactional
    public OtpResponse resendOtp(ResendOtpRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return generateAndSendPasswordResetOtp(user);
    }

    @Override
    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<PasswordOtp> otps = passwordOtpRepository
                .findByUserIdAndPurposeAndUsedFalseOrderByCreatedAtDesc(
                        user.getId(),
                        OtpPurpose.PASSWORD_RESET
                );

        PasswordOtp matchedOtp = otps.stream()
                .filter(otp -> !otp.isExpired())
                .filter(otp -> passwordEncoder.matches(request.otp(), otp.getOtpHash()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Invalid or expired OTP"));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        matchedOtp.markAsUsed();
        passwordOtpRepository.save(matchedOtp);

        return new ResetPasswordResponse("Password reset successful");
    }

    /**
     * Shared method for forgot password and resend OTP.
     *
     * Why we extracted it:
     * - Avoids duplicate code.
     * - Ensures forgot-password and resend-otp behave consistently.
     */
    private OtpResponse generateAndSendPasswordResetOtp(User user) {
        String rawOtp = OtpGenerator.generateSixDigitOtp();

        PasswordOtp passwordOtp = PasswordOtp.builder()
                .userId(user.getId())
                .otpHash(passwordEncoder.encode(rawOtp))
                .purpose(OtpPurpose.PASSWORD_RESET)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();

        passwordOtpRepository.save(passwordOtp);

        String smsMessage = "Your KOINS password reset OTP is " + rawOtp + ". It expires in 10 minutes.";

        smsProvider.sendSms(user.getPhoneNumber(), smsMessage);

        return new OtpResponse("OTP sent successfully", 10);
    }
}