package io.servewright.core.primitive;

import java.util.List;

public record PrimitiveDefinition(String name, boolean composable, List<PropSpec> props) {
}
