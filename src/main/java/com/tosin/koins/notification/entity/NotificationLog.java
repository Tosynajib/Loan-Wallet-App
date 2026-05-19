package com.tosin.koins.notification.entity;

import com.tosin.koins.common.enums.NotificationChannel;
import com.tosin.koins.common.enums.NotificationStatus;
import com.tosin.koins.common.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores every notification attempt.
 *
 * Why this matters:
 * - We can audit what notification was sent.
 * - We can troubleshoot failed notifications.
 * - We can avoid sending duplicate reminders later.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "notification_logs",
        indexes = {
                @Index(name = "idx_notification_logs_user_id", columnList = "user_id"),
                @Index(name = "idx_notification_logs_type", columnList = "type"),
                @Index(name = "idx_notification_logs_status", columnList = "status"),
                @Index(name = "idx_notification_logs_reference", columnList = "reference")
        }
)
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(nullable = false)
    private String recipient;

    @Column
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (this.status == null) {
            this.status = NotificationStatus.PENDING;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.failureReason = null;
    }

    public void markFailed(String reason) {
        this.status = NotificationStatus.FAILED;
        this.failureReason = reason;
    }
}