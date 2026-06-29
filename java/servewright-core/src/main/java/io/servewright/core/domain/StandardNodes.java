package io.servewright.core.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class StandardNodes {

    private StandardNodes() {
    }

    public static Node container(String id, List<Node> children) {
        return container(id, "vertical", children);
    }

    public static Node container(String id, String layout, List<Node> children) {
        Map<String, Object> props = new LinkedHashMap<>();
        if (!"vertical".equals(layout)) {
            props.put("layout", layout);
        }
        return new Node(id, "Container", props, children);
    }

    public static Node form(String id, String actionTarget, List<Node> children) {
        return new Node(id, "Form", Map.of("actionTarget", actionTarget), children);
    }

    public static Node group(String id, String label, List<Node> children) {
        Map<String, Object> props = new LinkedHashMap<>();
        if (label != null && !label.isBlank()) {
            props.put("label", label);
        }
        return new Node(id, "Group", props, children);
    }

    public static Node textInput(String id, String label) {
        return textInput(id, label, null, null, false);
    }

    public static Node textInput(String id, String label, String value, String placeholder, boolean required) {
        return textInput(id, label, value, placeholder, required, null, null, false, null);
    }

    public static Node textInput(
            String id,
            String label,
            String value,
            String placeholder,
            boolean required,
            Integer minLength,
            String pattern,
            boolean asyncValidation,
            String trigger) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("label", label);
        if (value != null) {
            props.put("value", value);
        }
        if (placeholder != null) {
            props.put("placeholder", placeholder);
        }
        if (required) {
            props.put("required", true);
        }
        if (minLength != null) {
            props.put("minLength", minLength);
        }
        if (pattern != null) {
            props.put("pattern", pattern);
        }
        if (asyncValidation) {
            props.put("asyncValidation", true);
        }
        if (trigger != null && !trigger.isBlank()) {
            props.put("trigger", trigger);
        }
        return new Node(id, "TextInput", props, List.of());
    }

    public static Node select(String id, String label, String value, List<Map<String, String>> options) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("label", label);
        props.put("options", options);
        if (value != null) {
            props.put("value", value);
        }
        return new Node(id, "Select", props, List.of());
    }

    public static Node checkbox(String id, String label, boolean checked) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("label", label);
        if (checked) {
            props.put("checked", true);
        }
        return new Node(id, "Checkbox", props, List.of());
    }

    public static Node button(String id, String label) {
        return button(id, label, "button");
    }

    public static Node button(String id, String label, String role) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("label", label);
        if (!"button".equals(role)) {
            props.put("role", role);
        }
        return new Node(id, "Button", props, List.of());
    }

    public static Node stat(String id, String label, String value) {
        return stat(id, label, value, null);
    }

    public static Node stat(String id, String label, String value, String delta) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("label", label);
        props.put("value", value);
        if (delta != null) {
            props.put("delta", delta);
        }
        return new Node(id, "Stat", props, List.of());
    }

    public static Node table(String id, List<Map<String, String>> columns, List<Map<String, Object>> rows) {
        return new Node(id, "Table", Map.of("columns", columns, "rows", rows), List.of());
    }
}
