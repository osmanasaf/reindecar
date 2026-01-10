package com.reindecar.entity.notification;

import com.reindecar.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NotificationPriority priority;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private NotificationStatus status;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @Size(max = 1000)
    @Column(length = 1000)
    private String message;

    @Size(max = 30)
    @Column(length = 30)
    private String referenceType;

    @Column
    private Long referenceId;

    @Column
    private Long recipientUserId;

    @Column
    private Instant readAt;

    @Column
    private Instant dismissedAt;

    @Column(nullable = false)
    private Instant createdAt;

    public static Notification create(
            NotificationType type,
            NotificationPriority priority,
            String title,
            String message,
            String referenceType,
            Long referenceId,
            Long recipientUserId) {

        Notification notification = new Notification();
        notification.type = type;
        notification.priority = priority;
        notification.status = NotificationStatus.PENDING;
        notification.title = title;
        notification.message = message;
        notification.referenceType = referenceType;
        notification.referenceId = referenceId;
        notification.recipientUserId = recipientUserId;
        notification.createdAt = Instant.now();
        return notification;
    }

    public void markAsRead() {
        if (this.status != NotificationStatus.READ) {
            this.status = NotificationStatus.READ;
            this.readAt = Instant.now();
        }
    }

    public void dismiss() {
        this.status = NotificationStatus.DISMISSED;
        this.dismissedAt = Instant.now();
    }

    public void markAsSent() {
        this.status = NotificationStatus.SENT;
    }

    public void markAsFailed() {
        this.status = NotificationStatus.FAILED;
    }

    public boolean isRead() {
        return this.status == NotificationStatus.READ;
    }

    public boolean isUrgent() {
        return this.priority == NotificationPriority.URGENT;
    }
}
