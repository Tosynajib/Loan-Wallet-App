package com.tosin.koins.loan.service;

import com.tosin.koins.common.enums.LoanStatus;
import com.tosin.koins.common.enums.RepaymentScheduleStatus;
import com.tosin.koins.common.enums.WalletStatus;
import com.tosin.koins.common.exception.BadRequestException;
import com.tosin.koins.common.security.CurrentUserProvider;
import com.tosin.koins.loan.dto.ApplyLoanRequest;
import com.tosin.koins.loan.entity.Loan;
import com.tosin.koins.loan.entity.LoanRepaymentSchedule;
import com.tosin.koins.loan.repository.LoanRepaymentScheduleRepository;
import com.tosin.koins.loan.repository.LoanRepository;
import com.tosin.koins.notification.service.NotificationService;
import com.tosin.koins.transaction.repository.TransactionRepository;
import com.tosin.koins.user.entity.User;
import com.tosin.koins.user.repository.UserRepository;
import com.tosin.koins.wallet.entity.Wallet;
import com.tosin.koins.wallet.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for loan business rules.
 */
@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private LoanRepaymentScheduleRepository repaymentScheduleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanServiceImpl loanService;

    @Test
    void applyForLoan_shouldThrowException_whenWalletIsNotFunded() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("user@test.com")
                .build();

        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .balance(BigDecimal.ZERO)
                .currency("NGN")
                .status(WalletStatus.ACTIVE)
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        ApplyLoanRequest request = new ApplyLoanRequest(BigDecimal.valueOf(1000), 30);

        assertThrows(BadRequestException.class, () -> loanService.applyForLoan(request));

        verify(loanRepository, never()).save(any());
        verify(repaymentScheduleRepository, never()).save(any());
    }

    @Test
    void applyForLoan_shouldThrowException_whenAmountExceedsThreeTimesWalletBalance() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("user@test.com")
                .build();

        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .balance(BigDecimal.valueOf(5000))
                .currency("NGN")
                .status(WalletStatus.ACTIVE)
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));

        ApplyLoanRequest request = new ApplyLoanRequest(BigDecimal.valueOf(20000), 30);

        assertThrows(BadRequestException.class, () -> loanService.applyForLoan(request));

        verify(loanRepository, never()).save(any());
        verify(repaymentScheduleRepository, never()).save(any());
    }

    @Test
    void applyForLoan_shouldCreatePendingLoanAndRepaymentSchedule_whenRequestIsValid() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("user@test.com")
                .build();

        Wallet wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .balance(BigDecimal.valueOf(5000))
                .currency("NGN")
                .status(WalletStatus.ACTIVE)
                .build();

        Loan savedLoan = Loan.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .loanAmount(BigDecimal.valueOf(10000))
                .interestRate(BigDecimal.valueOf(10.00))
                .durationDays(30)
                .status(LoanStatus.PENDING)
                .totalRepayableAmount(BigDecimal.valueOf(11000.00))
                .amountRepaid(BigDecimal.ZERO)
                .dueDate(LocalDate.now().plusDays(30))
                .build();

        LoanRepaymentSchedule savedSchedule = LoanRepaymentSchedule.builder()
                .id(UUID.randomUUID())
                .loanId(savedLoan.getId())
                .installmentNumber(1)
                .amountDue(BigDecimal.valueOf(11000.00))
                .amountPaid(BigDecimal.ZERO)
                .dueDate(LocalDate.now().plusDays(30))
                .status(RepaymentScheduleStatus.PENDING)
                .build();

        when(currentUserProvider.getCurrentUser()).thenReturn(user);
        when(walletRepository.findByUserId(user.getId())).thenReturn(Optional.of(wallet));
        when(loanRepository.save(any(Loan.class))).thenReturn(savedLoan);
        when(repaymentScheduleRepository.save(any(LoanRepaymentSchedule.class))).thenReturn(savedSchedule);

        ApplyLoanRequest request = new ApplyLoanRequest(BigDecimal.valueOf(10000), 30);

        var response = loanService.applyForLoan(request);

        assertNotNull(response);
        assertEquals(LoanStatus.PENDING, response.status());
        assertEquals(BigDecimal.valueOf(10000), response.loanAmount());
        assertEquals(1, response.repaymentSchedule().size());

        verify(loanRepository).save(any(Loan.class));
        verify(repaymentScheduleRepository).save(any(LoanRepaymentSchedule.class));
    }

    @Test
    void approveLoan_shouldApprovePendingLoanAndSendNotification() {
        UUID loanId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Loan loan = Loan.builder()
                .id(loanId)
                .userId(userId)
                .status(LoanStatus.PENDING)
                .loanAmount(BigDecimal.valueOf(10000))
                .totalRepayableAmount(BigDecimal.valueOf(11000))
                .amountRepaid(BigDecimal.ZERO)
                .build();

        User user = User.builder()
                .id(userId)
                .email("user@test.com")
                .fullName("Test User")
                .phoneNumber("+2348000000000")
                .build();

        when(loanRepository.findByIdForUpdate(loanId)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        var response = loanService.approveLoan(loanId);

        assertEquals("APPROVED", response.status());
        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        assertNotNull(loan.getApprovedAt());

        verify(notificationService).sendLoanApprovedNotification(user, loan);
    }

    @Test
    void approveLoan_shouldThrowException_whenLoanIsNotPending() {
        UUID loanId = UUID.randomUUID();

        Loan loan = Loan.builder()
                .id(loanId)
                .userId(UUID.randomUUID())
                .status(LoanStatus.DISBURSED)
                .loanAmount(BigDecimal.valueOf(10000))
                .totalRepayableAmount(BigDecimal.valueOf(11000))
                .amountRepaid(BigDecimal.ZERO)
                .build();

        when(loanRepository.findByIdForUpdate(loanId)).thenReturn(Optional.of(loan));

        assertThrows(BadRequestException.class, () -> loanService.approveLoan(loanId));

        verify(loanRepository, never()).save(any());
        verify(notificationService, never()).sendLoanApprovedNotification(any(), any());
    }
}