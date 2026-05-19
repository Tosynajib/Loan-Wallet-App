package com.tosin.koins.notification.service;

import com.tosin.koins.common.enums.NotificationChannel;
import com.tosin.koins.common.enums.NotificationStatus;
import com.tosin.koins.common.enums.NotificationType;
import com.tosin.koins.common.messaging.RabbitMqPublisher;
import com.tosin.koins.loan.entity.Loan;
import com.tosin.koins.notification.entity.NotificationLog;
import com.tosin.koins.notification.event.NotificationEvent;
import com.tosin.koins.notification.repository.NotificationLogRepository;
import com.tosin.koins.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for notification queuing.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private RabbitMqPublisher rabbitMqPublisher;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendLoanApprovedNotification_shouldCreateEmailAndSmsLogsAndPublishEvents() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("user@test.com")
                .phoneNumber("+2348000000000")
                .build();

        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .loanAmount(BigDecimal.valueOf(10000))
                .totalRepayableAmount(BigDecimal.valueOf(11000))
                .amountRepaid(BigDecimal.ZERO)
                .build();

        when(notificationLogRepository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> {
                    NotificationLog log = invocation.getArgument(0);
                    log.setId(UUID.randomUUID());
                    return log;
                });

        notificationService.sendLoanApprovedNotification(user, loan);

        verify(notificationLogRepository, times(2)).save(any(NotificationLog.class));
        verify(rabbitMqPublisher, times(2)).publishNotification(any(NotificationEvent.class));
    }

    @Test
    void sendPaymentConfirmationNotification_shouldCreateEmailAndSmsLogsAndPublishEvents() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("user@test.com")
                .phoneNumber("+2348000000000")
                .build();

        when(notificationLogRepository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> {
                    NotificationLog log = invocation.getArgument(0);
                    log.setId(UUID.randomUUID());
                    return log;
                });

        notificationService.sendPaymentConfirmationNotification(
                user,
                "FUND-123",
                BigDecimal.valueOf(5000),
                "NGN"
        );

        verify(notificationLogRepository, times(2)).save(any(NotificationLog.class));
        verify(rabbitMqPublisher, times(2)).publishNotification(any(NotificationEvent.class));
    }

    @Test
    void sendLoanApprovedNotification_shouldCreatePendingNotificationLogs() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .fullName("Test User")
                .email("user@test.com")
                .phoneNumber("+2348000000000")
                .build();

        Loan loan = Loan.builder()
                .id(UUID.randomUUID())
                .userId(user.getId())
                .loanAmount(BigDecimal.valueOf(10000))
                .totalRepayableAmount(BigDecimal.valueOf(11000))
                .amountRepaid(BigDecimal.ZERO)
                .build();

        when(notificationLogRepository.save(any(NotificationLog.class)))
                .thenAnswer(invocation -> {
                    NotificationLog log = invocation.getArgument(0);
                    log.setId(UUID.randomUUID());
                    return log;
                });

        notificationService.sendLoanApprovedNotification(user, loan);

        ArgumentCaptor<NotificationLog> captor = ArgumentCaptor.forClass(NotificationLog.class);

        verify(notificationLogRepository, times(2)).save(captor.capture());

        assertEquals(2, captor.getAllValues().size());

        assertTrue(
                captor.getAllValues()
                        .stream()
                        .allMatch(log -> log.getStatus() == NotificationStatus.PENDING)
        );

        assertTrue(
                captor.getAllValues()
                        .stream()
                        .anyMatch(log -> log.getChannel() == NotificationChannel.EMAIL)
        );

        assertTrue(
                captor.getAllValues()
                        .stream()
                        .anyMatch(log -> log.getChannel() == NotificationChannel.SMS)
        );

        assertTrue(
                captor.getAllValues()
                        .stream()
                        .allMatch(log -> log.getType() == NotificationType.LOAN_APPROVED)
        );
    }
}