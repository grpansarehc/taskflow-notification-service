package com.taskflow.notification_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // User ID from Keycloak (sub)
    private String title;
    private String message;
    private String type; // e.g., TASK_ASSIGNED
    private String referenceId; // e.g., Task ID

    @Column(name = "is_read")
    private boolean isRead;
    private LocalDateTime createdAt;
}
