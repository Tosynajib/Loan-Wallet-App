package com.tosin.koins.loan.repository;

import com.tosin.koins.common.enums.LoanStatus;
import com.tosin.koins.loan.entity.Loan;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    List<Loan> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Loan> findByStatusOrderByCreatedAtDesc(LoanStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from Loan l where l.id = :loanId")
    Optional<Loan> findByIdForUpdate(UUID loanId);

    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDate date);
}