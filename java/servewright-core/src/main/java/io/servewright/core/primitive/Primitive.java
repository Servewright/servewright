package io.servewright.core.primitive;

import java.util.ArrayList;
import java.util.List;

public final class Primitive {

    private Primitive() {
    }

    public static Builder define(String name) {
        return new Builder(name);
    }

    public static final class Builder {

        private final String name;
        private boolean composable;
        private final List<PropSpec> props = new ArrayList<>();

        private Builder(String name) {
            this.name = name;
        }

        public Builder prop(String name, PropType type, boolean required) {
            props.add(new PropSpec(name, type, required));
            return this;
        }

        public Builder composable() {
            this.composable = true;
            return this;
        }

        public PrimitiveDefinition build() {
            return new PrimitiveDefinition(name, composable, List.copyOf(props));
        }
    }
}
