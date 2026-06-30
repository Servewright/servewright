package io.servewright.core.port;

import io.servewright.core.domain.Transition;

public interface TransitionSerializer {

    String serialize(Transition transition);
}
