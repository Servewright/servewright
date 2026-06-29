package io.servewright.core;

import io.servewright.core.primitive.DuplicatePrimitiveException;
import io.servewright.core.primitive.Primitive;
import io.servewright.core.primitive.PrimitiveRegistry;
import io.servewright.core.primitive.PropType;
import io.servewright.core.primitive.StandardPrimitives;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimitiveRegistryTest {

    @Test
    void withStandardPrimitivesRegistersTenDefinitions() {
        PrimitiveRegistry registry = PrimitiveRegistry.withStandardPrimitives();

        assertEquals(10, StandardPrimitives.all().size());
        assertTrue(registry.find("Text").isPresent());
        assertTrue(registry.find("Table").isPresent());
    }

    @Test
    void rejectsDuplicateRegistrationAtBoot() {
        PrimitiveRegistry registry = new PrimitiveRegistry();
        registry.register(StandardPrimitives.text());

        assertThrows(DuplicatePrimitiveException.class, () -> registry.register(StandardPrimitives.text()));
    }

    @Test
    void primitiveBuilderCapturesComposableFlag() {
        var definition = Primitive.define("Demo")
                .prop("label", PropType.STRING, true)
                .composable()
                .build();

        assertTrue(definition.composable());
        assertEquals("Demo", definition.name());
    }
}
