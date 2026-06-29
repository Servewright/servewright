package io.servewright.core.application.port;

import io.servewright.core.domain.Action;
import io.servewright.core.domain.ActionResponse;

@FunctionalInterface
public interface ActionHandler {

    ActionResponse handle(Action action);
}
