package io.servewright.core;

import io.servewright.core.action.ActionRouter;
import io.servewright.core.application.command.ActionCommandHandler;
import io.servewright.core.domain.Action;
import io.servewright.core.domain.ActionResponse;
import io.servewright.core.domain.DemoViews;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ActionCommandHandlerTest {

    @Test
    void serverRejectsInvalidPayloadEvenIfClientWouldAccept() {
        ActionCommandHandler handler = new ActionCommandHandler(
                ActionRouter.builder()
                        .on("submit", "signup", action -> new ActionResponse(
                                View.of(action.screen(), Node.text("ignored", "ignored"))))
                        .build(),
                DemoViews::signupForm);

        Action invalid = new Action(
                "submit",
                "signup",
                "demo-form",
                0,
                Map.of("email", "not-an-email", "username", "validuser"));

        var response = handler.handle(invalid);
        @SuppressWarnings("unchecked")
        List<String> emailErrors = (List<String>) response.view().root()
                .children().get(0)
                .children().get(0)
                .props()
                .get("errors");

        assertFalse(emailErrors.isEmpty());
    }

    @Test
    void validPayloadRoutesToHandler() {
        ActionCommandHandler handler = new ActionCommandHandler(
                ActionRouter.builder()
                        .on("submit", "signup", action -> new ActionResponse(
                                View.of(action.screen(), Node.text("success", "OK"))))
                        .build(),
                DemoViews::signupForm);

        Action valid = new Action(
                "submit",
                "signup",
                "demo-form",
                0,
                Map.of("email", "user@example.com", "username", "validuser"));

        var response = handler.handle(valid);
        assertEquals("OK", response.view().root().props().get("content"));
    }
}
