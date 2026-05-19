package com.tosin.koins.loan.service;

import com.tosin.koins.common.enums.LoanStatus;
import com.tosin.koins.common.enums.RepaymentScheduleStatus;
import com.tosin.koins.integration.sms.SmsProvider;
import com.tosin.koins.loan.entity.Loan;
import com.tosin.koins.loan.entity.LoanRepaymentSchedule;
import com.tosin.koins.loan.repository.LoanRepaymentScheduleRepository;
import com.tosin.koins.loan.repository.LoanRepository;
import com.tosin.koins.notification.service.NotificationService;
import com.tosin.koins.user.entity.User;
import com.tosin.koins.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Handles scheduled loan monitoring tasks.
 *
 * This class contains the actual business logic for:
 * - marking overdue repayment schedules
 * - marking loans as defaulted
 * - sending repayment reminders
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanMonitoringServiceImpl implements LoanMonitoringService {

    private final LoanRepository loanRepository;
    private final LoanRepaymentScheduleRepository repaymentScheduleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void markOverdueLoans() {
        LocalDate today = LocalDate.now();

        List<LoanRepaymentSchedule> overdueSchedules =
                repaymentScheduleRepository.findByStatusInAndDueDateBefore(
                        List.of(
                                RepaymentScheduleStatus.PENDING,
                                RepaymentScheduleStatus.PARTIALLY_PAID
                        ),
                        today
                );

        if (overdueSchedules.isEmpty()) {
            log.info("No overdue repayment schedules found.");
            return;
        }

        for (LoanRepaymentSchedule schedule : overdueSchedules) {
            schedule.markOverdue();
            repaymentScheduleRepository.save(schedule);

            Loan loan = loanRepository.findById(schedule.getLoanId())
                    .orElse(null);

            if (loan == null) {
                log.warn("Loan not found for repayment schedule id={}", schedule.getId());
                continue;
            }

            if (loan.getStatus() == LoanStatus.DISBURSED) {
                loan.setStatus(LoanStatus.DEFAULTED);
                loanRepository.save(loan);

                log.info(
                        "Loan marked as DEFAULTED. loanId={}, scheduleId={}",
                        loan.getId(),
                        schedule.getId()
                );
            }
        }
    }

    @Override
    public void sendRepaymentReminders() {
        LocalDate reminderDate = LocalDate.now().plusDays(1);

        List<LoanRepaymentSchedule> schedulesDueTomorrow =
                repaymentScheduleRepository.findByStatusInAndDueDate(
                        List.of(
                                RepaymentScheduleStatus.PENDING,
                                RepaymentScheduleStatus.PARTIALLY_PAID
                        ),
                        reminderDate
                );

        if (schedulesDueTomorrow.isEmpty()) {
            log.info("No repayment reminders due for {}", reminderDate);
            return;
        }

        for (LoanRepaymentSchedule schedule : schedulesDueTomorrow) {
            Loan loan = loanRepository.findById(schedule.getLoanId())
                    .orElse(null);

            if (loan == null) {
                log.warn("Loan not found for reminder schedule id={}", schedule.getId());
                continue;
            }

            User user = userRepository.findById(loan.getUserId())
                    .orElse(null);

            if (user == null) {
                log.warn("User not found for loan id={}", loan.getId());
                continue;
            }

            String message = "Reminder: Your KOINS loan repayment of NGN "
                    + schedule.getAmountDue()
                    + " is due on "
                    + schedule.getDueDate()
                    + ".";

            notificationService.sendRepaymentReminderNotification(user, loan, schedule);

            log.info(
                    "Repayment reminder processed. userId={}, loanId={}, dueDate={}",
                    user.getId(),
                    loan.getId(),
                    schedule.getDueDate()
            );

        }
    }
}