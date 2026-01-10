package com.reindecar.dto.notification;

import com.reindecar.entity.notification.NotificationPriority;
import com.reindecar.entity.notification.NotificationStatus;
import com.reindecar.entity.notification.NotificationType;

import java.time.Instant;

public record NotificationResponse(
    Long id,
    NotificationType type,
    NotificationPriority priority,
    NotificationStatus status,
    String title,
    String message,
    String referenceType,
    Long referenceId,
    Instant readAt,
    Instant createdAt,
    boolean isRead,
    boolean isUrgent
) {}
