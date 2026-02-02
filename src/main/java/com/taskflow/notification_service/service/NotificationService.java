package com.taskflow.notification_service.service;

import com.taskflow.notification_service.dto.NotificationPreferenceResponse;
import com.taskflow.notification_service.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotifications(String userId);

    List<Notification> getUnreadNotifications(String userId);

    long getUnreadCount(String userId);

    void markAsRead(Long notificationId);

    void markAllAsRead(String userId);

    void deleteNotification(Long notificationId);

    NotificationPreferenceResponse getPreferences(String userId);

    NotificationPreferenceResponse updatePreferences(String userId, NotificationPreferenceResponse preferences);

    // Internal use
    Notification saveNotification(String userId, String title, String message, String type, String referenceId);
}
