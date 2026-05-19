package com.tosin.koins.loan.service;

import com.tosin.koins.common.enums.LoanStatus;
import com.tosin.koins.common.enums.RepaymentScheduleStatus;
import com.tosin.koins.common.enums.TransactionStatus;
import com.tosin.koins.common.enums.TransactionType;
import com.tosin.koins.common.exception.BadRequestException;
import com.tosin.koins.common.exception.NotFoundException;
import com.tosin.koins.common.security.CurrentUserProvider;
import com.tosin.koins.common.util.ReferenceGenerator;
import com.tosin.koins.loan.dto.ApplyLoanRequest;
import com.tosin.koins.loan.dto.LoanActionResponse;
import com.tosin.koins.loan.dto.LoanResponse;
import com.tosin.koins.loan.dto.RepayLoanRequest;
import com.tosin.koins.loan.dto.RepaymentScheduleResponse;
import com.tosin.koins.loan.entity.Loan;
import com.tosin.koins.loan.entity.LoanRepaymentSchedule;
import com.tosin.koins.loan.repository.LoanRepaymentScheduleRepository;
import com.tosin.koins.loan.repository.LoanRepository;
import com.tosin.koins.notification.service.NotificationService;
import com.tosin.koins.transaction.entity.Transaction;
import com.tosin.koins.transaction.repository.TransactionRepository;
import com.tosin.koins.user.entity.User;
import com.tosin.koins.user.repository.UserRepository;
import com.tosin.koins.wallet.entity.Wallet;
import com.tosin.koins.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Loan business logic.
 *
 * This class enforces the core loan rules:
 * - User must have a funded wallet.
 * - Loan amount must not exceed 3x wallet balance.
 * - Loan must be approved before disbursement.
 * - Loan repayment debits wallet and updates loan state.
 * - Every financial movement creates a transaction log.
 */
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private static final BigDecimal DEFAULT_INTEREST_RATE = BigDecimal.valueOf(10.00);

    private final LoanRepository loanRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;
    private final LoanRepaymentScheduleRepository repaymentScheduleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public LoanResponse applyForLoan(ApplyLoanRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        validateWalletIsFunded(wallet);
        validateLoanAmountWithinLimit(request.amount(), wallet.getBalance());

        BigDecimal totalRepayableAmount = calculateTotalRepayableAmount(
                request.amount(),
                DEFAULT_INTEREST_RATE
        );

        LocalDate dueDate = LocalDate.now().plusDays(request.durationDays());

        Loan loan = Loan.builder()
                .userId(currentUser.getId())
                .loanAmount(request.amount())
                .interestRate(DEFAULT_INTEREST_RATE)
                .durationDays(request.durationDays())
                .status(LoanStatus.PENDING)
                .totalRepayableAmount(totalRepayableAmount)
                .amountRepaid(BigDecimal.ZERO)
                .dueDate(dueDate)
                .build();

        Loan savedLoan = loanRepository.save(loan);

        LoanRepaymentSchedule savedSchedule = createInitialRepaymentSchedule(
                savedLoan,
                totalRepayableAmount,
                dueDate
        );

        return LoanResponse.from(
                savedLoan,
                List.of(RepaymentScheduleResponse.from(savedSchedule))
        );
    }

    @Override
    public List<LoanResponse> getMyLoans() {
        User currentUser = currentUserProvider.getCurrentUser();

        return loanRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toLoanResponseWithSchedules)
                .toList();
    }

    @Override
    public LoanResponse getMyLoanById(UUID loanId) {
        User currentUser = currentUserProvider.getCurrentUser();

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        validateLoanOwnership(loan, currentUser);

        return toLoanResponseWithSchedules(loan);
    }

    @Override
    public List<LoanResponse> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(this::toLoanResponseWithSchedules)
                .toList();
    }

    @Override
    @Transactional
    public LoanActionResponse approveLoan(UUID loanId) {
        Loan loan = loanRepository.findByIdForUpdate(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BadRequestException("Only pending loans can be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovedAt(LocalDateTime.now());

        loanRepository.save(loan);

        User user = userRepository.findById(loan.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        notificationService.sendLoanApprovedNotification(user, loan);

        return new LoanActionResponse(
                loan.getId(),
                loan.getStatus().name(),
                "Loan approved successfully"
        );
    }

    @Override
    @Transactional
    public LoanActionResponse disburseLoan(UUID loanId) {
        Loan loan = loanRepository.findByIdForUpdate(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new BadRequestException("Loan must be approved before disbursement");
        }

        Wallet wallet = walletRepository.findByUserId(loan.getUserId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        wallet.credit(loan.getLoanAmount());

        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursedAt(LocalDateTime.now());

        Transaction transaction = Transaction.builder()
                .userId(loan.getUserId())
                .walletId(wallet.getId())
                .type(TransactionType.LOAN_DISBURSEMENT)
                .amount(loan.getLoanAmount())
                .status(TransactionStatus.SUCCESSFUL)
                .reference(ReferenceGenerator.generate("LOAN-DISBURSE"))
                .description("Loan disbursement")
                .build();

        walletRepository.save(wallet);
        loanRepository.save(loan);
        transactionRepository.save(transaction);

        User user = userRepository.findById(loan.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        notificationService.sendLoanDisbursedNotification(user, loan);

        return new LoanActionResponse(
                loan.getId(),
                loan.getStatus().name(),
                "Loan disbursed successfully"
        );
    }

    @Override
    @Transactional
    public LoanActionResponse repayLoan(UUID loanId, RepayLoanRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        Loan loan = loanRepository.findByIdForUpdate(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));

        validateLoanOwnership(loan, currentUser);

        if (loan.getStatus() != LoanStatus.DISBURSED && loan.getStatus() != LoanStatus.DEFAULTED) {
            throw new BadRequestException("Only disbursed or defaulted loans can be repaid");
        }

        BigDecimal outstandingAmount = loan.outstandingAmount();

        if (request.amount().compareTo(outstandingAmount) > 0) {
            throw new BadRequestException("Repayment amount cannot exceed outstanding loan amount");
        }

        Wallet wallet = walletRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new NotFoundException("Wallet not found"));

        wallet.debit(request.amount());

        loan.setAmountRepaid(loan.getAmountRepaid().add(request.amount()));

        LoanRepaymentSchedule schedule = repaymentScheduleRepository
                .findFirstByLoanIdAndStatusInOrderByInstallmentNumberAsc(
                        loan.getId(),
                        List.of(
                                RepaymentScheduleStatus.PENDING,
                                RepaymentScheduleStatus.PARTIALLY_PAID,
                                RepaymentScheduleStatus.OVERDUE
                        )
                )
                .orElseThrow(() -> new NotFoundException("Repayment schedule not found"));

        schedule.applyPayment(request.amount());

        if (loan.isFullyRepaid()) {
            loan.setStatus(LoanStatus.REPAID);
            loan.setRepaidAt(LocalDateTime.now());
        }

        Transaction transaction = Transaction.builder()
                .userId(currentUser.getId())
                .walletId(wallet.getId())
                .type(TransactionType.REPAYMENT)
                .amount(request.amount())
                .status(TransactionStatus.SUCCESSFUL)
                .reference(ReferenceGenerator.generate("LOAN-REPAY"))
                .description("Loan repayment")
                .build();

        walletRepository.save(wallet);
        repaymentScheduleRepository.save(schedule);
        loanRepository.save(loan);
        transactionRepository.save(transaction);

        notificationService.sendSuccessfulRepaymentNotification(currentUser, loan);

        return new LoanActionResponse(
                loan.getId(),
                loan.getStatus().name(),
                "Loan repayment successful"
        );
    }

    private void validateWalletIsFunded(Wallet wallet) {
        if (wallet.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Only users with funded wallets can apply for loans");
        }
    }

    private void validateLoanAmountWithinLimit(BigDecimal requestedAmount, BigDecimal walletBalance) {
        BigDecimal maxAllowedLoan = walletBalance.multiply(BigDecimal.valueOf(3));

        if (requestedAmount.compareTo(maxAllowedLoan) > 0) {
            throw new BadRequestException("Loan amount cannot exceed 3x wallet balance");
        }
    }

    private void validateLoanOwnership(Loan loan, User currentUser) {
        if (!loan.getUserId().equals(currentUser.getId())) {
            throw new NotFoundException("Loan not found");
        }
    }

    private LoanRepaymentSchedule createInitialRepaymentSchedule(
            Loan savedLoan,
            BigDecimal totalRepayableAmount,
            LocalDate dueDate
    ) {
        LoanRepaymentSchedule schedule = LoanRepaymentSchedule.builder()
                .loanId(savedLoan.getId())
                .installmentNumber(1)
                .amountDue(totalRepayableAmount)
                .amountPaid(BigDecimal.ZERO)
                .dueDate(dueDate)
                .status(RepaymentScheduleStatus.PENDING)
                .build();

        return repaymentScheduleRepository.save(schedule);
    }

    private LoanResponse toLoanResponseWithSchedules(Loan loan) {
        List<RepaymentScheduleResponse> schedules = repaymentScheduleRepository
                .findByLoanIdOrderByInstallmentNumberAsc(loan.getId())
                .stream()
                .map(RepaymentScheduleResponse::from)
                .toList();

        return LoanResponse.from(loan, schedules);
    }

    private BigDecimal calculateTotalRepayableAmount(BigDecimal principal, BigDecimal interestRate) {
        BigDecimal interest = principal
                .multiply(interestRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return principal.add(interest);
    }
}