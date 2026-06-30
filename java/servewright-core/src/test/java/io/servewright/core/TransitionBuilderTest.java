package io.servewright.core;

import io.servewright.core.domain.Node;
import io.servewright.core.domain.StandardNodes;
import io.servewright.core.domain.View;
import io.servewright.core.transition.PatchApplier;
import io.servewright.core.transition.TransitionBuilder;
import io.servewright.core.transition.TransitionDesyncException;
import io.servewright.core.transition.UnknownPatchTargetException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransitionBuilderTest {

    @Test
    void rejectsPatchTargetingUnknownId() {
        View view = View.of("demo-form", sampleForm());

        assertThrows(UnknownPatchTargetException.class, () -> TransitionBuilder.basedOn(view)
                .setError("missing", List.of("Error"))
                .build());
    }

    @Test
    void patchSequenceProducesExpectedTree() {
        View view = View.of("demo-form", sampleForm());

        var transition = TransitionBuilder.basedOn(view)
                .setError("email", List.of("Invalid format"))
                .replace("submit", StandardNodes.button("submit", "Retry", "submit"))
                .build();

        View updated = PatchApplier.apply(view, transition);

        assertEquals(1, updated.stateVersion());
        @SuppressWarnings("unchecked")
        List<String> errors = (List<String>) updated.root()
                .children().get(0)
                .children().get(0)
                .props()
                .get("errors");
        assertEquals(List.of("Invalid format"), errors);
        assertEquals("Retry", updated.root().children().get(0).children().get(1).props().get("label"));
    }

    @Test
    void stateVersionIncrementsOnBuild() {
        View view = new View("1.0", "0.1.0", "demo-form", 3, sampleForm());

        var transition = TransitionBuilder.basedOn(view)
                .setLoading("email", true)
                .build();

        assertEquals(3, transition.basedOn());
        assertEquals(4, transition.stateVersion());
    }

    @Test
    void desyncThrowsWhenBasedOnMismatch() {
        View view = View.of("demo-form", sampleForm());
        var transition = TransitionBuilder.basedOn(view).setError("email", List.of("x")).build();

        assertThrows(TransitionDesyncException.class, () -> PatchApplier.apply(
                new View(view.servewrightVersion(), view.schemaVersion(), view.screen(), 99, view.root()),
                transition));
    }

    @Test
    void insertAndRemovePatchesWork() {
        View view = View.of("demo-form", sampleForm());
        var insertTransition = TransitionBuilder.basedOn(view)
                .insert("personal-group", 1, Node.text("hint", "Check your email"))
                .build();
        View withHint = PatchApplier.apply(view, insertTransition);

        var removeTransition = TransitionBuilder.basedOn(withHint)
                .remove("hint")
                .build();
        View removed = PatchApplier.apply(withHint, removeTransition);

        assertTrue(removed.root().children().get(0).children().stream()
                .noneMatch(node -> "hint".equals(node.id())));
    }

    private static Node sampleForm() {
        return StandardNodes.form(
                "signup-form",
                "signup",
                List.of(
                        StandardNodes.group(
                                "personal-group",
                                "Personal",
                                List.of(
                                        StandardNodes.textInput("email", "Email", "", null, true),
                                        StandardNodes.button("submit", "Submit", "submit")))));
    }
}
