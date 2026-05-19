package com.tosin.koins.notification.service;

import com.tosin.koins.common.enums.NotificationChannel;
import com.tosin.koins.common.enums.NotificationStatus;
import com.tosin.koins.common.enums.NotificationType;
import com.tosin.koins.common.messaging.RabbitMqPublisher;
import com.tosin.koins.loan.entity.Loan;
import com.tosin.koins.loan.entity.LoanRepaymentSchedule;
import com.tosin.koins.notification.entity.NotificationLog;
import com.tosin.koins.notification.event.NotificationEvent;
import com.tosin.koins.notification.repository.NotificationLogRepository;
import com.tosin.koins.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Creates notification logs and publishes notification events.
 *
 * Actual SMS/email sending happens asynchronously in NotificationEventConsumer.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final RabbitMqPublisher rabbitMqPublisher;

    @Override
    public void sendLoanApprovedNotification(User user, Loan loan) {
        String subject = "Your KOINS loan has been approved";
        String message = "Hello " + user.getFullName()
                + ", your loan request of NGN " + loan.getLoanAmount()
                + " has been approved. It will be disbursed shortly.";

        queueEmail(user, loan.getId().toString(), NotificationType.LOAN_APPROVED, subject, message);
        queueSms(user, loan.getId().toString(), NotificationType.LOAN_APPROVED, message);
    }

    @Override
    public void sendLoanDisbursedNotification(User user, Loan loan) {
        String subject = "Your KOINS loan has been disbursed";
        String message = "Hello " + user.getFullName()
                + ", your loan of NGN " + loan.getLoanAmount()
                + " has been disbursed to your wallet. Total repayment amount is NGN "
                + loan.getTotalRepayableAmount() + ".";

        queueEmail(user, loan.getId().toString(), NotificationType.LOAN_DISBURSED, subject, message);
        queueSms(user, loan.getId().toString(), NotificationType.LOAN_DISBURSED, message);
    }

    @Override
    public void sendSuccessfulRepaymentNotification(User user, Loan loan) {
        String subject = "Loan repayment received";
        String message = "Hello " + user.getFullName()
                + ", your loan repayment was successful. Outstanding balance is NGN "
                + safeAmount(loan.outstandingAmount()) + ".";

        queueEmail(user, loan.getId().toString(), NotificationType.LOAN_REPAYMENT_SUCCESSFUL, subject, message);
        queueSms(user, loan.getId().toString(), NotificationType.LOAN_REPAYMENT_SUCCESSFUL, message);
    }

    @Override
    public void sendRepaymentReminderNotification(User user, Loan loan, LoanRepaymentSchedule schedule) {
        String reference = schedule.getId().toString();

        boolean alreadyQueuedSms = notificationLogRepository.existsByReferenceAndTypeAndChannel(
                reference,
                NotificationType.LOAN_REPAYMENT_REMINDER,
                NotificationChannel.SMS
        );

        if (alreadyQueuedSms) {
            log.info("Repayment reminder already queued/sent for scheduleId={}", reference);
            return;
        }

        String subject = "KOINS loan repayment reminder";
        String message = "Hello " + user.getFullName()
                + ", reminder: your KOINS loan repayment of NGN "
                + schedule.getAmountDue()
                + " is due on "
                + schedule.getDueDate()
                + ".";

        queueEmail(user, reference, NotificationType.LOAN_REPAYMENT_REMINDER, subject, message);
        queueSms(user, reference, NotificationType.LOAN_REPAYMENT_REMINDER, message);
    }

    public void sendPaymentConfirmationNotification(
            User user,
            String transactionReference,
            BigDecimal amount,
            String currency
    ) {
        String subject = "Wallet funding successful";
        String message = "Hello " + user.getFullName()
                + ", your wallet funding of " + currency + " " + amount
                + " was successful. Reference: " + transactionReference + ".";

        queueEmail(user, transactionReference, NotificationType.PAYMENT_CONFIRMATION, subject, message);
        queueSms(user, transactionReference, NotificationType.PAYMENT_CONFIRMATION, message);
    }

    private void queueSms(User user, String reference, NotificationType type, String message) {
        NotificationLog logRecord = createLog(
                user,
                reference,
                type,
                NotificationChannel.SMS,
                user.getPhoneNumber(),
                null,
                message
        );

        NotificationLog savedLog = notificationLogRepository.save(logRecord);

        rabbitMqPublisher.publishNotification(
                new NotificationEvent(
                        savedLog.getId(),
                        user.getId(),
                        reference,
                        type,
                        NotificationChannel.SMS,
                        user.getPhoneNumber(),
                        null,
                        message
                )
        );
    }

    private void queueEmail(
            User user,
            String reference,
            NotificationType type,
            String subject,
            String message
    ) {
        NotificationLog logRecord = createLog(
                user,
                reference,
                type,
                NotificationChannel.EMAIL,
                user.getEmail(),
                subject,
                message
        );

        NotificationLog savedLog = notificationLogRepository.save(logRecord);

        rabbitMqPublisher.publishNotification(
                new NotificationEvent(
                        savedLog.getId(),
                        user.getId(),
                        reference,
                        type,
                        NotificationChannel.EMAIL,
                        user.getEmail(),
                        subject,
                        message
                )
        );
    }

    private NotificationLog createLog(
            User user,
            String reference,
            NotificationType type,
            NotificationChannel channel,
            String recipient,
            String subject,
            String message
    ) {
        return NotificationLog.builder()
                .userId(user.getId())
                .reference(reference)
                .type(type)
                .channel(channel)
                .status(NotificationStatus.PENDING)
                .recipient(recipient)
                .subject(subject)
                .message(message)
                .build();
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}