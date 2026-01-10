package com.reindecar.service.notification;

import com.reindecar.dto.notification.NotificationCountResponse;
import com.reindecar.dto.notification.NotificationResponse;
import com.reindecar.entity.notification.Notification;
import com.reindecar.entity.notification.NotificationPriority;
import com.reindecar.entity.notification.NotificationStatus;
import com.reindecar.entity.notification.NotificationType;
import com.reindecar.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private static final List<NotificationStatus> UNREAD_STATUSES = 
        List.of(NotificationStatus.PENDING, NotificationStatus.SENT);

    public Page<NotificationResponse> getByUserId(Long userId, Pageable pageable) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(this::toResponse);
    }

    public List<NotificationResponse> getUnreadByUserId(Long userId) {
        return notificationRepository.findByRecipientUserIdAndStatusInOrderByCreatedAtDesc(userId, UNREAD_STATUSES)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public NotificationCountResponse getCountByUserId(Long userId) {
        long total = notificationRepository.count();
        long unread = notificationRepository.countByRecipientUserIdAndStatusIn(userId, UNREAD_STATUSES);
        return new NotificationCountResponse(total, unread, 0L);
    }

    @Transactional
    public void createNotification(
            NotificationType type,
            NotificationPriority priority,
            String title,
            String message,
            String referenceType,
            Long referenceId,
            Long recipientUserId) {

        if (isDuplicate(type, referenceType, referenceId)) {
            log.debug("Duplicate notification skipped: type={}, ref={}:{}", type, referenceType, referenceId);
            return;
        }

        Notification notification = Notification.create(
            type, priority, title, message, referenceType, referenceId, recipientUserId
        );
        notificationRepository.save(notification);
        log.info("Notification created: type={}, title={}", type, title);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Transactional
    public void dismiss(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.dismiss();
        notificationRepository.save(notification);
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository
            .findByRecipientUserIdAndStatusInOrderByCreatedAtDesc(userId, UNREAD_STATUSES);
        notifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(notifications);
        return notifications.size();
    }

    private boolean isDuplicate(NotificationType type, String referenceType, Long referenceId) {
        if (referenceType == null || referenceId == null) {
            return false;
        }
        List<Notification> existing = notificationRepository.findActiveByTypeAndReference(
            type, referenceType, referenceId, UNREAD_STATUSES);
        return !existing.isEmpty();
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
            n.getId(),
            n.getType(),
            n.getPriority(),
            n.getStatus(),
            n.getTitle(),
            n.getMessage(),
            n.getReferenceType(),
            n.getReferenceId(),
            n.getReadAt(),
            n.getCreatedAt(),
            n.isRead(),
            n.isUrgent()
        );
    }
}

