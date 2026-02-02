package com.taskflow.notification_service.service;

import com.taskflow.notification_service.dto.TaskAssignedEvent;

public interface EmailService {
    void sendTaskAssignmentEmail(TaskAssignedEvent event);
}
