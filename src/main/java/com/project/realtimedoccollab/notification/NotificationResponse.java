package com.project.realtimedoccollab.notification;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String message,
        boolean read,
        Long documentId,
        Instant createdAt
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getMessage(),
                notification.isRead(),
                notification.getDocumentId(),
                notification.getCreatedAt()
        );
    }
}
