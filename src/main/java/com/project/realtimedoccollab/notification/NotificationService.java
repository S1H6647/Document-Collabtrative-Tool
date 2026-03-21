package com.project.realtimedoccollab.notification;

import com.project.realtimedoccollab.exception.ResourceNotFoundException;
import com.project.realtimedoccollab.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Async("notificationExecutor")
    public void sendNotificationAsync(User recipient, String recipientEmail, NotificationType type, String message, Long documentId) {
        try {
            Notification notification = Notification.builder()
                    .recipient(recipient)
                    .type(type)
                    .message(message)
                    .documentId(documentId)
                    .build();

            Notification saved = notificationRepository.save(notification);
            log.debug("Notification saved: type={} recipientId={}", type, recipient.getId());

            messagingTemplate.convertAndSendToUser(
                    recipientEmail,
                    "/queue/notifications",
                    NotificationResponse.from(saved)
            );

        } catch (Exception e) {
            // Never let async failures silently vanish — log them
            log.error("Failed to persist notification for user {}: {}", recipient.getId(), e.getMessage(), e);
        }
    }

    // Query methods
    @Transactional
    public Page<NotificationResponse> getNotifications(UUID recipientId, int page, int size) {
        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(recipientId, PageRequest.of(page, size))
                .map(NotificationResponse::from);
    }

    @Transactional
    public long getUnreadCount(UUID recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    //  Mutation methods

    @Transactional
    public NotificationResponse markAsRead(Long notificationId, UUID recipientId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getRecipient().getId().equals(recipientId)) {
            throw new AccessDeniedException("Not your notification");
        }

        notification.markAsRead();
        return NotificationResponse.from(notificationRepository.save(notification));
    }

    @Transactional
    public int markAllAsRead(UUID recipientId) {
        return notificationRepository.markAllReadByRecipientId(recipientId);
    }
}
