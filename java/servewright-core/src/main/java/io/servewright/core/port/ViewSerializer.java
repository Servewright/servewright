package io.servewright.core.port;

import io.servewright.core.domain.View;

public interface ViewSerializer {

    String serialize(View view);
}
