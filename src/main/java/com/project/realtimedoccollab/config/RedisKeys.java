package com.project.realtimedoccollab.config;

import java.util.UUID;

public final class RedisKeys {

    public static final long PRESENCE_TTL_SECONDS = 300;

    private RedisKeys() {
    }

    public static String presenceKey(long documentId) {
        return "presence:document:" + documentId;
    }

    public static String documentChannel(long documentId) {
        return "document." + documentId;
    }

    public static String notificationThrottleKey(long documentId, UUID userId) {
        return "notification:throttle:document:" + documentId + ":user:" + userId;
    }
}
