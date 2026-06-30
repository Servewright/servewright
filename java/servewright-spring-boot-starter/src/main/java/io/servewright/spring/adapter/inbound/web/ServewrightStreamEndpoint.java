package io.servewright.spring.adapter.inbound.web;

import io.servewright.spring.adapter.outbound.SseTransitionPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class ServewrightStreamEndpoint {

    private final SseTransitionPublisher transitionPublisher;

    public ServewrightStreamEndpoint(SseTransitionPublisher transitionPublisher) {
        this.transitionPublisher = transitionPublisher;
    }

    @GetMapping(value = "/servewright/stream/{screen}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable("screen") String screen) {
        return transitionPublisher.subscribe(screen);
    }
}
