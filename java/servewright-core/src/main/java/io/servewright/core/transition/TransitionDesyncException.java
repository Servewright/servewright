package io.servewright.core.transition;

public final class TransitionDesyncException extends RuntimeException {

    public TransitionDesyncException(int expectedBasedOn, int actualStateVersion) {
        super("Transition basedOn=" + expectedBasedOn + " does not match client stateVersion=" + actualStateVersion);
    }
}
