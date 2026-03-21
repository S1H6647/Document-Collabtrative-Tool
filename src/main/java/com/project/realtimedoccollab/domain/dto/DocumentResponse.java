package com.project.realtimedoccollab.domain.dto;

import com.project.realtimedoccollab.domain.document.Document;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        long id,
        String title,
        String content,
        UUID ownerId,
        String ownerName,
        Instant createdAt,
        Instant updatedAt
) {
    public static DocumentResponse from(Document document) {
        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getContent(),
                document.getOwner().getId(),
                document.getOwner().getName(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
