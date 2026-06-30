package io.servewright.core.transition;

public final class UnknownPatchTargetException extends RuntimeException {

    public UnknownPatchTargetException(String op, String target) {
        super("Patch target not found for op='" + op + "' target='" + target + "'");
    }
}
