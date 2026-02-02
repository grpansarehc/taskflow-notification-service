package com.taskflow.notification_service.service.impl;

import com.taskflow.notification_service.dto.TaskAssignedEvent;
import com.taskflow.notification_service.entity.NotificationLog;
import com.taskflow.notification_service.repository.NotificationLogRepository;
import com.taskflow.notification_service.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final NotificationLogRepository logRepository;

    @Override
    public void sendTaskAssignmentEmail(TaskAssignedEvent event) {
        log.info("Preparing email for task: {}", event.getTitle());

        Context context = new Context();
        context.setVariable("task", event);

        String process = templateEngine.process("task-assigned", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        NotificationLog notificationLog = NotificationLog.builder()
                .taskId(event.getTaskId())
                .recipientEmail(event.getAssignee().getEmail())
                .subject("New Task Assigned: " + event.getTitle())
                .sentAt(LocalDateTime.now())
                .build();

        try {
            helper.setTo(event.getAssignee().getEmail());
            helper.setSubject("New Task Assigned: " + event.getTitle());
            helper.setText(process, true);

            mailSender.send(mimeMessage);

            notificationLog.setStatus(NotificationLog.NotificationStatus.SENT);
            log.info("Email sent successfully to {}", event.getAssignee().getEmail());
        } catch (MessagingException | org.springframework.mail.MailException e) {
            log.error("Failed to send email to {}: {}", event.getAssignee().getEmail(), e.getMessage());
            notificationLog.setStatus(NotificationLog.NotificationStatus.FAILED);
            notificationLog.setErrorMessage(e.getMessage());
            throw new RuntimeException("Email sending failed", e); // Re-throw for Kafka retry
        } finally {
            logRepository.save(notificationLog);
        }
    }
}
