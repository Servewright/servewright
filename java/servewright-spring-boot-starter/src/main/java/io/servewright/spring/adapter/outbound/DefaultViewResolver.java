package io.servewright.spring.adapter.outbound;

import io.servewright.core.application.port.ViewResolver;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;

public final class DefaultViewResolver implements ViewResolver {

    @Override
    public View resolve(String screen) {
        return View.of(screen, Node.text("greeting", "Bonjour"));
    }
}
