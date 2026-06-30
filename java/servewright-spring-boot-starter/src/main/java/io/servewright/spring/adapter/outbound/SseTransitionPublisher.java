package io.servewright.spring.adapter.outbound;

import io.servewright.core.domain.Transition;
import io.servewright.core.port.TransitionSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseTransitionPublisher implements io.servewright.core.application.port.TransitionPublisher {

    private final TransitionSerializer transitionSerializer;
    private final Map<String, List<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public SseTransitionPublisher(TransitionSerializer transitionSerializer) {
        this.transitionSerializer = transitionSerializer;
    }

    @Override
    public void publish(String screen, Transition transition) {
        String payload = transitionSerializer.serialize(transition);
        for (SseEmitter emitter : subscribers.getOrDefault(screen, List.of())) {
            try {
                emitter.send(SseEmitter.event().name("transition").data(payload));
            } catch (IOException exception) {
                emitter.completeWithError(exception);
            }
        }
    }

    public SseEmitter subscribe(String screen) {
        SseEmitter emitter = new SseEmitter(0L);
        subscribers.computeIfAbsent(screen, key -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(screen, emitter));
        emitter.onTimeout(() -> remove(screen, emitter));
        emitter.onError(error -> remove(screen, emitter));
        return emitter;
    }

    private void remove(String screen, SseEmitter emitter) {
        List<SseEmitter> emitters = subscribers.get(screen);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }
}
