package io.servewright.spring.adapter.inbound.web;

import io.servewright.core.application.command.ActionCommandHandler;
import io.servewright.core.domain.Action;
import io.servewright.core.domain.ActionResponse;
import io.servewright.core.port.TransitionSerializer;
import io.servewright.core.port.ViewSerializer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServewrightActionEndpoint {

    private final ActionCommandHandler actionCommandHandler;
    private final ViewSerializer viewSerializer;
    private final TransitionSerializer transitionSerializer;

    public ServewrightActionEndpoint(
            ActionCommandHandler actionCommandHandler,
            ViewSerializer viewSerializer,
            TransitionSerializer transitionSerializer) {
        this.actionCommandHandler = actionCommandHandler;
        this.viewSerializer = viewSerializer;
        this.transitionSerializer = transitionSerializer;
    }

    @PostMapping(value = "/servewright/action", produces = MediaType.APPLICATION_JSON_VALUE)
    public String postAction(@RequestBody Action action) {
        ActionResponse response = actionCommandHandler.handle(action);
        if (response.hasTransition()) {
            return "{\"transition\":" + transitionSerializer.serialize(response.transition()) + "}";
        }
        return "{\"view\":" + viewSerializer.serialize(response.view()) + "}";
    }
}
