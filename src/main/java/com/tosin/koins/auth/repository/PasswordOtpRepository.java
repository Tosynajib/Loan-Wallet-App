package com.tosin.koins.auth.repository;

import com.tosin.koins.auth.entity.PasswordOtp;
import com.tosin.koins.common.enums.OtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PasswordOtpRepository extends JpaRepository<PasswordOtp, UUID> {

    /**
     * We fetch recent unused OTPs for the user and purpose.
     * During verification, we check which one matches the raw OTP supplied.
     */
    List<PasswordOtp> findByUserIdAndPurposeAndUsedFalseOrderByCreatedAtDesc(
            UUID userId,
            OtpPurpose purpose
    );
}