package com.pj.planjourney.domain.sse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EventService {
    private final RedisMessageListenerContainer redisContainer;
    private final static String NOTIFICATION_TOPIC = "notification:";

    public void subscribeNotificationEvent(Long userId, SseEmitter emitter) {
        ChannelTopic topic = new ChannelTopic(NOTIFICATION_TOPIC + userId);

        MessageListener listener = (message, pattern) -> {
            try {
                String messageBody = new String(message.getBody());
                emitter.send(SseEmitter.event().data(messageBody));
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        };
        redisContainer.addMessageListener(new MessageListenerAdapter(listener), topic);
    }
}
