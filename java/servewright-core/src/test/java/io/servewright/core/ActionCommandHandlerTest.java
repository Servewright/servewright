package io.servewright.core;

import io.servewright.core.action.ActionRouter;
import io.servewright.core.application.ViewState;
import io.servewright.core.application.command.ActionCommandHandler;
import io.servewright.core.domain.Action;
import io.servewright.core.domain.ActionResponse;
import io.servewright.core.domain.DemoViews;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionCommandHandlerTest {

    @Test
    void serverRejectsInvalidPayloadEvenIfClientWouldAccept() {
        ActionCommandHandler handler = createHandler(new AtomicReference<>());
        Action invalid = new Action(
                "submit",
                "signup",
                "demo-form",
                0,
                Map.of("email", "not-an-email", "username", "validuser"));

        var response = handler.handle(invalid);

        assertNotNull(response.transition());
        ViewState state = new ViewState(DemoViews::signupForm);
        View updated = state.applyTransition("demo-form", response.transition());
        @SuppressWarnings("unchecked")
        List<String> emailErrors = (List<String>) updated.root()
                .children().get(0)
                .children().get(0)
                .props()
                .get("errors");
        assertFalse(emailErrors.isEmpty());
    }

    @Test
    void validPayloadRoutesToHandlerWithTransition() {
        AtomicReference<View> lastView = new AtomicReference<>();
        ActionCommandHandler handler = createHandler(lastView);
        Action valid = new Action(
                "submit",
                "signup",
                "demo-form",
                0,
                Map.of("email", "user@example.com", "username", "validuser"));

        var response = handler.handle(valid);

        assertNotNull(response.transition());
        assertTrue(lastView.get().root().props().get("content").toString().startsWith("Registered"));
    }

    @Test
    void staleStateVersionReturnsFullViewForResync() {
        ActionCommandHandler handler = createHandler(new AtomicReference<>());
        Action stale = new Action(
                "submit",
                "signup",
                "demo-form",
                99,
                Map.of("email", "user@example.com", "username", "validuser"));

        var response = handler.handle(stale);

        assertNotNull(response.view());
        assertFalse(response.hasTransition());
        assertEquals("demo-form", response.view().screen());
    }

    private static ActionCommandHandler createHandler(AtomicReference<View> lastView) {
        ViewState viewState = new ViewState(DemoViews::signupForm);
        return new ActionCommandHandler(
                ActionRouter.builder()
                        .on("submit", "signup", action -> ActionResponse.ofView(
                                View.of(
                                        action.screen(),
                                        Node.text("success", "Registered " + action.payload().get("email")))))
                        .build(),
                DemoViews::signupForm,
                viewState,
                (screen, transition) -> lastView.set(viewState.getOrLoad(screen)));
    }
}
