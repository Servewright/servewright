package io.servewright.core.action;

import io.servewright.core.application.port.ActionHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ActionRouter {

    private final Map<ActionRoute, ActionHandler> handlers;

    private ActionRouter(Map<ActionRoute, ActionHandler> handlers) {
        this.handlers = Map.copyOf(handlers);
    }

    public Optional<ActionHandler> route(String type, String target) {
        return Optional.ofNullable(handlers.get(new ActionRoute(type, target)));
    }

    public List<ActionRoute> routes() {
        return List.copyOf(handlers.keySet());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final Map<ActionRoute, ActionHandler> handlers = new LinkedHashMap<>();

        public Builder on(String type, String target, ActionHandler handler) {
            ActionRoute route = new ActionRoute(type, target);
            if (handlers.containsKey(route)) {
                throw new DuplicateActionHandlerException(type, target);
            }
            handlers.put(route, handler);
            return this;
        }

        public ActionRouter build() {
            return new ActionRouter(handlers);
        }
    }
}
