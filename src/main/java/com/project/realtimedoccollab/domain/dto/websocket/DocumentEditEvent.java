package com.project.realtimedoccollab.domain.dto.websocket;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record DocumentEditEvent(
        long documentId,
        UUID editorId,
        String editorEmail,
        String content,
        Instant editedAt
) {
    public static DocumentEditEvent from(DocumentEditRequest request, UUID userId, String email) {
        return new DocumentEditEvent(
                request.documentId(),
                userId,
                email,
                request.content(),
                Instant.now()
        );
    }
}

