package io.servewright.core.action;

public final class DuplicateActionHandlerException extends RuntimeException {

    public DuplicateActionHandlerException(String type, String target) {
        super("Duplicate action handler for type='" + type + "' target='" + target + "'");
    }
}
