package com.pj.planjourney.domain.sse.controller;

import com.pj.planjourney.domain.sse.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping(value = "/stream/notifications/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@PathVariable Long userId) {
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);
        emitter.onTimeout(emitter::complete);
        emitter.onError(emitter::completeWithError);

        eventService.subscribeNotificationEvent(userId, emitter);

        return emitter;
    }

}
