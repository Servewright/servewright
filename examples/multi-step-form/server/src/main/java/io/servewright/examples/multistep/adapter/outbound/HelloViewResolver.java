package io.servewright.examples.multistep.adapter.outbound;

import io.servewright.core.application.port.ViewResolver;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import org.springframework.stereotype.Component;

@Component
public class HelloViewResolver implements ViewResolver {

    @Override
    public View resolve(String screen) {
        return View.of(screen, Node.text("greeting", "Bonjour"));
    }
}
