package io.servewright.core.domain;

import java.util.List;
import java.util.Objects;

public record Transition(int basedOn, int stateVersion, List<Patch> patches) {

    public Transition {
        if (basedOn < 0 || stateVersion < 0) {
            throw new IllegalArgumentException("state versions must be >= 0");
        }
        if (stateVersion <= basedOn) {
            throw new IllegalArgumentException("stateVersion must be greater than basedOn");
        }
        Objects.requireNonNull(patches, "patches");
        patches = List.copyOf(patches);
    }
}
