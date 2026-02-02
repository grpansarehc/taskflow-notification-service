package com.taskflow.notification_service.service.impl;

import com.taskflow.notification_service.dto.NotificationPreferenceResponse;
import com.taskflow.notification_service.entity.Notification;
import com.taskflow.notification_service.entity.NotificationPreference;
import com.taskflow.notification_service.repository.NotificationPreferenceRepository;
import com.taskflow.notification_service.repository.NotificationRepository;
import com.taskflow.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
    }

    @Override
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
            pushUnreadCount(n.getUserId());
        });
    }

    @Override
    public void markAllAsRead(String userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, false);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
        pushUnreadCount(userId);
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            String userId = n.getUserId();
            notificationRepository.delete(n);
            pushUnreadCount(userId);
        });
    }

    @Override
    public NotificationPreferenceResponse getPreferences(String userId) {
        NotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> preferenceRepository.save(NotificationPreference.builder().userId(userId).build()));

        return mapToResponse(pref);
    }

    @Override
    public NotificationPreferenceResponse updatePreferences(String userId, NotificationPreferenceResponse updated) {
        NotificationPreference pref = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> NotificationPreference.builder().userId(userId).build());

        pref.setEmailEnabled(updated.isEmailEnabled());
        pref.setPushEnabled(updated.isPushEnabled());
        pref.setTaskAssignedEnabled(updated.isTaskAssignedEnabled());
        pref.setTaskUpdatedEnabled(updated.isTaskUpdatedEnabled());
        pref.setDeadlineReminderEnabled(updated.isDeadlineReminderEnabled());

        return mapToResponse(preferenceRepository.save(pref));
    }

    @Override
    public Notification saveNotification(String userId, String title, String message, String type, String referenceId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // Push notification via WebSocket
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, saved);

        // Push updated unread count
        pushUnreadCount(userId);

        return saved;
    }

    private void pushUnreadCount(String userId) {
        long count = getUnreadCount(userId);
        messagingTemplate.convertAndSend("/topic/notifications/" + userId + "/count", count);
    }

    private NotificationPreferenceResponse mapToResponse(NotificationPreference pref) {
        return NotificationPreferenceResponse.builder()
                .emailEnabled(pref.isEmailEnabled())
                .pushEnabled(pref.isPushEnabled())
                .taskAssignedEnabled(pref.isTaskAssignedEnabled())
                .taskUpdatedEnabled(pref.isTaskUpdatedEnabled())
                .deadlineReminderEnabled(pref.isDeadlineReminderEnabled())
                .build();
    }
}
