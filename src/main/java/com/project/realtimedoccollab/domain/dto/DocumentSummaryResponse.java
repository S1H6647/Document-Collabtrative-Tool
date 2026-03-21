package com.project.realtimedoccollab.domain.dto;

import com.project.realtimedoccollab.domain.document.Document;

import java.time.Instant;

public record DocumentSummaryResponse(
        Long id,
        String title,
        String ownerName,
        Instant updatedAt
) {
    public static DocumentSummaryResponse from(Document document) {
        return new DocumentSummaryResponse(
                document.getId(),
                document.getTitle(),
                document.getOwner().getName(),
                Instant.now()
        );
    }
}
