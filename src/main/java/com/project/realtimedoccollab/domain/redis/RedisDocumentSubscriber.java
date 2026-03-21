package com.project.realtimedoccollab.domain.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.realtimedoccollab.domain.dto.websocket.DocumentEditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisDocumentSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, @Nullable byte[] pattern) {

        try {
            // JSON String -> Usable Java object
            String json = new String(message.getBody());
            DocumentEditEvent event = objectMapper.readValue(json, DocumentEditEvent.class);

            log.debug("Received edit event from Redis for document {}", event.documentId());

            messagingTemplate.convertAndSend(
                    "/topic/document." + event.documentId() + ".edits",
                    event
            );

        } catch (JsonMappingException e) {
            log.error("Failed to deserialize edit event from Redis", e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
