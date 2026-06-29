package io.servewright.core.application.port;

import io.servewright.core.domain.Action;

import java.util.List;

@FunctionalInterface
public interface AsyncValidationHandler {

    List<String> validate(Action action, String fieldId);
}
