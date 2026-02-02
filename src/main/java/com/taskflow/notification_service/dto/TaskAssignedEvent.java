package com.taskflow.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskAssignedEvent {
    private UUID taskId;
    private UUID assigneeId;
    private String title;
    private String description;
    private String priority;
    private LocalDateTime dueDate;
    private UserDetails assignee;
    private String assignedBy;
    private String projectName;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UserDetails {
        private String name;
        private String email;
    }
}
