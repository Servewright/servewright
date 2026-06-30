package io.servewright.core.validation;

import io.servewright.core.domain.Node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class NodeTree {

    private NodeTree() {
    }

    public static Optional<Node> findNodeById(Node root, String id) {
        if (root.id().equals(id)) {
            return Optional.of(root);
        }
        for (Node child : root.children()) {
            Optional<Node> found = findNodeById(child, id);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    public static Optional<Node> findFormByActionTarget(Node root, String target) {
        if ("Form".equals(root.type())) {
            Object actionTarget = root.props().get("actionTarget");
            if (Objects.equals(actionTarget, target)) {
                return Optional.of(root);
            }
        }
        for (Node child : root.children()) {
            Optional<Node> found = findFormByActionTarget(child, target);
            if (found.isPresent()) {
                return found;
            }
        }
        return Optional.empty();
    }

    public static List<Node> collectInputNodes(Node root) {
        List<Node> inputs = new ArrayList<>();
        collectInputNodes(root, inputs);
        return inputs;
    }

    private static void collectInputNodes(Node node, List<Node> inputs) {
        if (isInputType(node.type())) {
            inputs.add(node);
        }
        for (Node child : node.children()) {
            collectInputNodes(child, inputs);
        }
    }

    public static Node applyFieldStates(
            Node node,
            Map<String, Object> values,
            Map<String, List<String>> errors,
            Map<String, Boolean> validating) {
        Map<String, Object> props = new LinkedHashMap<>(node.props());
        if (isInputType(node.type())) {
            if (values.containsKey(node.id())) {
                props.put("value", stringValue(values.get(node.id())));
            }
            if (errors.containsKey(node.id())) {
                props.put("errors", List.copyOf(errors.get(node.id())));
            } else {
                props.remove("errors");
            }
            if (validating.getOrDefault(node.id(), false)) {
                props.put("validating", true);
            } else {
                props.remove("validating");
            }
        }

        List<Node> children = node.children().stream()
                .map(child -> applyFieldStates(child, values, errors, validating))
                .toList();

        return new Node(node.id(), node.type(), props, children);
    }

    private static boolean isInputType(String type) {
        return "TextInput".equals(type) || "Select".equals(type) || "Checkbox".equals(type);
    }

    private static String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
