package com.taskflow.notification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_preferences")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Builder.Default
    private boolean emailEnabled = true;
    @Builder.Default
    private boolean pushEnabled = true;
    @Builder.Default
    private boolean taskAssignedEnabled = true;
    @Builder.Default
    private boolean taskUpdatedEnabled = true;
    @Builder.Default
    private boolean deadlineReminderEnabled = true;
}
