package com.tosin.koins.common.config;

import com.tosin.koins.common.enums.AccountStatus;
import com.tosin.koins.common.enums.UserRole;
import com.tosin.koins.common.enums.WalletStatus;
import com.tosin.koins.user.entity.User;
import com.tosin.koins.user.repository.UserRepository;
import com.tosin.koins.wallet.entity.Wallet;
import com.tosin.koins.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Seeds the first SUPER_ADMIN account when the application starts.
 *
 * Why:
 * - Public signup should not create admins.
 * - The system needs an initial admin to approve/disburse loans.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitialAdminSeeder implements CommandLineRunner {

    private final AdminProperties adminProperties;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        String email = adminProperties.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            log.info("Initial admin already exists: {}", email);
            return;
        }

        User admin = User.builder()
                .fullName(adminProperties.fullName())
                .email(email)
                .phoneNumber(adminProperties.phoneNumber())
                .password(passwordEncoder.encode(adminProperties.password()))
                .bvnOrNin(adminProperties.bvnOrNin())
                .status(AccountStatus.ACTIVE)
                .role(UserRole.SUPER_ADMIN)
                .build();

        User savedAdmin = userRepository.save(admin);

        Wallet adminWallet = Wallet.builder()
                .userId(savedAdmin.getId())
                .balance(BigDecimal.ZERO)
                .currency("NGN")
                .status(WalletStatus.ACTIVE)
                .build();

        walletRepository.save(adminWallet);

        log.info("Initial SUPER_ADMIN seeded successfully: {}", email);
    }
}