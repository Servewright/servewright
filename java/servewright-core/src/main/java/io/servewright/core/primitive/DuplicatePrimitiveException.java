package io.servewright.core.primitive;

public final class DuplicatePrimitiveException extends RuntimeException {

    public DuplicatePrimitiveException(String name) {
        super("Duplicate primitive registration: " + name);
    }
}
