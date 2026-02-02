package com.taskflow.notification_service.consumer;

import com.taskflow.notification_service.dto.TaskAssignedEvent;
import com.taskflow.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskAssignmentConsumer {

    private final EmailService emailService;
    private final com.taskflow.notification_service.service.NotificationService notificationService;

    @KafkaListener(topics = "task-assigned", groupId = "notification-group")
    public void consumeTaskAssignedEvent(TaskAssignedEvent event) {
        log.info("Received TaskAssignedEvent for task ID: {}", event.getTaskId());
        try {
            // 1. Get user preferences
            // Use UUID as the identifier for notifications and preferences
            String userId = event.getAssigneeId().toString();
            var preferences = notificationService.getPreferences(userId);

            // 2. Save in-app notification
            String message = String.format("%s assigned a new task: %s in project %s",
                    event.getAssignedBy(), event.getTitle(), event.getProjectName());

            notificationService.saveNotification(
                    userId,
                    "New Task Assigned",
                    message,
                    "TASK_ASSIGNED",
                    event.getTaskId().toString());

            // 3. Send Email if enabled
            if (preferences.isEmailEnabled() && preferences.isTaskAssignedEnabled()) {
                emailService.sendTaskAssignmentEmail(event);
            } else {
                log.info("Email notification skipped due to user preferences for {}", userId);
            }

        } catch (Exception e) {
            log.error("Error processing event for task {}: {}", event.getTaskId(), e.getMessage());
            throw e;
        }
    }
}
