package com.taskflow.notification_service.repository;

import com.taskflow.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    List<Notification> findByUserIdAndIsReadOrderByCreatedAtDesc(String userId, boolean isRead);

    long countByUserIdAndIsRead(String userId, boolean isRead);
}
