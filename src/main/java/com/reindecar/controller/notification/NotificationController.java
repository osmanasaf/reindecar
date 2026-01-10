package com.reindecar.controller.notification;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.notification.NotificationCountResponse;
import com.reindecar.dto.notification.NotificationResponse;
import com.reindecar.service.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get notifications", description = "Returns user notifications")
    public ApiResponse<Page<NotificationResponse>> getNotifications(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Long userId = getUserId(authentication);
        Page<NotificationResponse> notifications = notificationService.getByUserId(userId, pageable);
        return ApiResponse.success(notifications);
    }

    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Returns unread notifications")
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications(Authentication authentication) {
        Long userId = getUserId(authentication);
        List<NotificationResponse> notifications = notificationService.getUnreadByUserId(userId);
        return ApiResponse.success(notifications);
    }

    @GetMapping("/count")
    @Operation(summary = "Get notification count", description = "Returns unread and urgent counts")
    public ApiResponse<NotificationCountResponse> getNotificationCount(Authentication authentication) {
        Long userId = getUserId(authentication);
        NotificationCountResponse count = notificationService.getCountByUserId(userId);
        return ApiResponse.success(count);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Marks notification as read")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ApiResponse.success("Notification marked as read", null);
    }

    @PatchMapping("/{id}/dismiss")
    @Operation(summary = "Dismiss notification", description = "Dismisses notification")
    public ApiResponse<Void> dismiss(@PathVariable Long id) {
        notificationService.dismiss(id);
        return ApiResponse.success("Notification dismissed", null);
    }

    @PostMapping("/mark-all-read")
    @Operation(summary = "Mark all as read", description = "Marks all notifications as read")
    public ApiResponse<Integer> markAllAsRead(Authentication authentication) {
        Long userId = getUserId(authentication);
        int count = notificationService.markAllAsRead(userId);
        return ApiResponse.success(count + " notifications marked as read", count);
    }

    private Long getUserId(Authentication authentication) {
        return 1L;
    }
}
