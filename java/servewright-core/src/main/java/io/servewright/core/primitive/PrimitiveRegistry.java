package io.servewright.core.primitive;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class PrimitiveRegistry {

    private final Map<String, PrimitiveDefinition> definitions = new LinkedHashMap<>();

    public void register(PrimitiveDefinition definition) {
        if (definitions.containsKey(definition.name())) {
            throw new DuplicatePrimitiveException(definition.name());
        }
        definitions.put(definition.name(), definition);
    }

    public void registerAll(Collection<PrimitiveDefinition> definitions) {
        definitions.forEach(this::register);
    }

    public Optional<PrimitiveDefinition> find(String name) {
        return Optional.ofNullable(definitions.get(name));
    }

    public static PrimitiveRegistry withStandardPrimitives() {
        PrimitiveRegistry registry = new PrimitiveRegistry();
        registry.registerAll(StandardPrimitives.all());
        return registry;
    }
}
