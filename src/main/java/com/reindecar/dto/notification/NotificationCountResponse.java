package com.reindecar.dto.notification;

public record NotificationCountResponse(
    long total,
    long unread,
    long urgent
) {}
