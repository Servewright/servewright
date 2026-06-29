package io.servewright.core.application.port;

import io.servewright.core.domain.View;

@FunctionalInterface
public interface ViewResolver {

    View resolve(String screen);
}
