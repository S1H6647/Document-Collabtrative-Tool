package com.project.realtimedoccollab.domain.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.realtimedoccollab.config.RedisKeys;
import com.project.realtimedoccollab.domain.dto.websocket.DocumentEditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisDocumentPublisher {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, String> redisTemplate;

    public void publishEditEvent(UUID userId, long documentId, String content) {
        try {
            DocumentEditEvent event = DocumentEditEvent.builder()
                    .documentId(documentId)
                    .editorId(userId)
                    .content(content)
                    .editedAt(Instant.now())
                    .build();

            // Java object -> JSON String
            String json = objectMapper.writeValueAsString(event);
            String channel = RedisKeys.documentChannel(documentId);

            redisTemplate.convertAndSend(channel, json);

            log.debug("Published edit event to Redis channel: {}", channel);

        } catch (JsonProcessingException e) {
            // Don't let serialization failure kill the edit — content is already saved
            log.error("Failed to publish edit event to Redis for document {}", documentId, e);
        }
    }
}
