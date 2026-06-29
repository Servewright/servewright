package io.servewright.core.validation;

import io.servewright.core.domain.Node;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class FieldValidator {

    private FieldValidator() {
    }

    public static Map<String, List<String>> validateForm(Node formRoot, Map<String, Object> payload) {
        Map<String, List<String>> errors = new LinkedHashMap<>();
        for (Node field : NodeTree.collectInputNodes(formRoot)) {
            String value = stringValue(payload.get(field.id()));
            List<String> fieldErrors = validateInputNode(field, value);
            if (!fieldErrors.isEmpty()) {
                errors.put(field.id(), fieldErrors);
            }
        }
        return errors;
    }

    public static List<String> validateInputNode(Node field, String value) {
        return switch (field.type()) {
            case "TextInput" -> DeclarativeValidator.validateTextInput(field.props(), value);
            default -> List.of();
        };
    }

    public static Optional<Node> findField(Node root, String fieldId) {
        return NodeTree.collectInputNodes(root).stream()
                .filter(node -> node.id().equals(fieldId))
                .findFirst();
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
