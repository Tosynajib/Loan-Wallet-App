package com.tosin.koins.notification.service;

import com.tosin.koins.loan.entity.Loan;
import com.tosin.koins.loan.entity.LoanRepaymentSchedule;
import com.tosin.koins.user.entity.User;

import java.math.BigDecimal;

public interface NotificationService {

    void sendLoanApprovedNotification(User user, Loan loan);

    void sendLoanDisbursedNotification(User user, Loan loan);

    void sendSuccessfulRepaymentNotification(User user, Loan loan);

    void sendRepaymentReminderNotification(User user, Loan loan, LoanRepaymentSchedule schedule);

    void sendPaymentConfirmationNotification(
            User user,
            String transactionReference,
            BigDecimal amount,
            String currency
    );
}