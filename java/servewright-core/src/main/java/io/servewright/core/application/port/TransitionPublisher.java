package io.servewright.core.application.port;

import io.servewright.core.domain.Transition;

@FunctionalInterface
public interface TransitionPublisher {

    void publish(String screen, Transition transition);
}
