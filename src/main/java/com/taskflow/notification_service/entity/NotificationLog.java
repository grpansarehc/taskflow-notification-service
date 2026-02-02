package com.taskflow.notification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_logs")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID taskId;
    private String recipientEmail;
    private String subject;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String errorMessage;
    private LocalDateTime sentAt;
    private int retryCount;

    public enum NotificationStatus {
        SENT, FAILED, RETRYING
    }
}
