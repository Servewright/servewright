package io.servewright.spring.adapter.outbound;

import io.servewright.core.domain.Transition;
import io.servewright.core.port.TransitionSerializer;
import org.springframework.stereotype.Component;

@Component
public class JacksonTransitionSerializer implements TransitionSerializer {

    private final io.servewright.core.infrastructure.JsonTransitionSerializer delegate =
            new io.servewright.core.infrastructure.JsonTransitionSerializer();

    @Override
    public String serialize(Transition transition) {
        return delegate.serialize(transition);
    }
}
