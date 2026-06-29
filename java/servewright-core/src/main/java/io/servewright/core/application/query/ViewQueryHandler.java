package io.servewright.core.application.query;

import io.servewright.core.application.port.ViewResolver;
import io.servewright.core.domain.View;

public final class ViewQueryHandler {

    private final ViewResolver viewResolver;

    public ViewQueryHandler(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    public View handle(GetViewQuery query) {
        return viewResolver.resolve(query.screen());
    }
}
