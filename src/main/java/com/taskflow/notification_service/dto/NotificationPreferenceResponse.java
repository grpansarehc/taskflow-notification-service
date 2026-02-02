package com.taskflow.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationPreferenceResponse {
    private boolean emailEnabled;
    private boolean pushEnabled;
    private boolean taskAssignedEnabled;
    private boolean taskUpdatedEnabled;
    private boolean deadlineReminderEnabled;
}
