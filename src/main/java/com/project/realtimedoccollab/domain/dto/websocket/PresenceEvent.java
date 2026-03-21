package com.project.realtimedoccollab.domain.dto.websocket;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record PresenceEvent(
        long documentId,
        UUID userId,
        String email,
        Type type,
        Set<String> activeEditors,
        Instant occurredAt
) {
    public static PresenceEvent joined(long documentId, UUID userId, String email, Set<String> activeEditors) {
        return new PresenceEvent(documentId, userId, email, Type.JOINED, activeEditors, Instant.now());
    }

    public static PresenceEvent left(long documentId, UUID userId, String email, Set<String> activeEditors) {
        return new PresenceEvent(documentId, userId, email, Type.LEFT, activeEditors, Instant.now());
    }

    public enum Type {
        JOINED,
        LEFT
    }
}