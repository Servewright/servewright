package io.servewright.core.primitive;

import java.util.List;

public final class StandardPrimitives {

    private StandardPrimitives() {
    }

    public static List<PrimitiveDefinition> all() {
        return List.of(
                text(),
                container(),
                form(),
                group(),
                textInput(),
                select(),
                checkbox(),
                button(),
                stat(),
                table());
    }

    public static PrimitiveDefinition text() {
        return Primitive.define("Text")
                .prop("content", PropType.STRING, true)
                .prop("emphasis", PropType.STRING, false)
                .build();
    }

    public static PrimitiveDefinition container() {
        return Primitive.define("Container")
                .prop("layout", PropType.STRING, false)
                .composable()
                .build();
    }

    public static PrimitiveDefinition form() {
        return Primitive.define("Form")
                .prop("actionTarget", PropType.STRING, true)
                .composable()
                .build();
    }

    public static PrimitiveDefinition group() {
        return Primitive.define("Group")
                .prop("label", PropType.STRING, false)
                .composable()
                .build();
    }

    public static PrimitiveDefinition textInput() {
        return Primitive.define("TextInput")
                .prop("label", PropType.STRING, true)
                .prop("value", PropType.STRING, false)
                .prop("placeholder", PropType.STRING, false)
                .prop("required", PropType.BOOLEAN, false)
                .build();
    }

    public static PrimitiveDefinition select() {
        return Primitive.define("Select")
                .prop("label", PropType.STRING, true)
                .prop("value", PropType.STRING, false)
                .prop("options", PropType.ARRAY, true)
                .build();
    }

    public static PrimitiveDefinition checkbox() {
        return Primitive.define("Checkbox")
                .prop("label", PropType.STRING, true)
                .prop("checked", PropType.BOOLEAN, false)
                .build();
    }

    public static PrimitiveDefinition button() {
        return Primitive.define("Button")
                .prop("label", PropType.STRING, true)
                .prop("role", PropType.STRING, false)
                .build();
    }

    public static PrimitiveDefinition stat() {
        return Primitive.define("Stat")
                .prop("label", PropType.STRING, true)
                .prop("value", PropType.STRING, true)
                .prop("delta", PropType.STRING, false)
                .build();
    }

    public static PrimitiveDefinition table() {
        return Primitive.define("Table")
                .prop("columns", PropType.ARRAY, true)
                .prop("rows", PropType.ARRAY, true)
                .build();
    }
}
