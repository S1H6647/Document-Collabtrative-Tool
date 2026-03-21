package com.project.realtimedoccollab.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // Paginated fetch for a user's notifications (newest first)
    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);

    // count unread
    int countByRecipientIdAndReadFalse(UUID recipientId);

    // Bulk mark-all-read for a user
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipient.id = :recipientId AND n.read = false")
    int markAllReadByRecipientId(UUID recipientId);
}
