package io.servewright.core.domain;

import java.util.Map;
import java.util.Objects;

public record Action(
        String type,
        String target,
        String screen,
        int stateVersion,
        Map<String, Object> payload) {

    public Action {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(screen, "screen");
        Objects.requireNonNull(payload, "payload");
        if (stateVersion < 0) {
            throw new IllegalArgumentException("stateVersion must be >= 0");
        }
        payload = Map.copyOf(payload);
    }
}
