package com.reindecar.repository.notification;

import com.reindecar.entity.notification.Notification;
import com.reindecar.entity.notification.NotificationStatus;
import com.reindecar.entity.notification.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Notification> findByRecipientUserIdAndStatusInOrderByCreatedAtDesc(
        Long userId, List<NotificationStatus> statuses);

    long countByRecipientUserIdAndStatusIn(Long userId, List<NotificationStatus> statuses);

    @Query("SELECT n FROM Notification n WHERE n.type = :type AND n.referenceType = :refType AND n.referenceId = :refId AND n.status IN :statuses")
    List<Notification> findActiveByTypeAndReference(
        @Param("type") NotificationType type, 
        @Param("refType") String refType, 
        @Param("refId") Long refId,
        @Param("statuses") List<NotificationStatus> statuses);
}

