package io.servewright.core;

import io.servewright.core.action.ActionRouter;
import io.servewright.core.action.DuplicateActionHandlerException;
import io.servewright.core.application.port.ActionHandler;
import io.servewright.core.domain.Action;
import io.servewright.core.domain.ActionResponse;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ActionRouterTest {

    @Test
    void routesToRegisteredHandler() {
        ActionRouter router = ActionRouter.builder()
                .on("submit", "signup", action -> new ActionResponse(
                        View.of(action.screen(), Node.text("ok", "Done"))))
                .build();

        Action action = new Action("submit", "signup", "demo-form", 0, Map.of("email", "a@b.c"));
        ActionResponse response = router.route("submit", "signup").orElseThrow().handle(action);

        assertEquals("Done", response.view().root().props().get("content"));
    }

    @Test
    void duplicateRegistrationFailsAtBuild() {
        ActionHandler handler = action -> new ActionResponse(View.of("x", Node.text("x", "x")));

        assertThrows(DuplicateActionHandlerException.class, () -> ActionRouter.builder()
                .on("submit", "signup", handler)
                .on("submit", "signup", handler)
                .build());
    }

    @Test
    void unknownRouteReturnsEmpty() {
        ActionRouter router = ActionRouter.builder().build();
        assertTrue(router.route("submit", "missing").isEmpty());
    }
}
