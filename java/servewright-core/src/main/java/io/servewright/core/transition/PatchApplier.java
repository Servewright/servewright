package io.servewright.core.transition;

import io.servewright.core.domain.Node;
import io.servewright.core.domain.Patch;
import io.servewright.core.domain.Transition;
import io.servewright.core.domain.View;
import io.servewright.core.validation.NodeTree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class PatchApplier {

    private PatchApplier() {
    }

    public static View apply(View view, Transition transition) {
        if (transition.basedOn() != view.stateVersion()) {
            throw new TransitionDesyncException(transition.basedOn(), view.stateVersion());
        }

        Node root = view.root();
        for (Patch patch : transition.patches()) {
            root = applyPatch(root, patch);
        }

        return new View(
                view.servewrightVersion(),
                view.schemaVersion(),
                view.screen(),
                transition.stateVersion(),
                root);
    }

    private static Node applyPatch(Node root, Patch patch) {
        return switch (patch) {
            case Patch.Replace replace -> replaceNode(root, replace.target(), replace.node());
            case Patch.Insert insert -> insertNode(root, insert.parent(), insert.index(), insert.node());
            case Patch.Remove remove -> removeNode(root, remove.target());
            case Patch.SetError setError -> setErrors(root, setError.target(), setError.errors());
            case Patch.SetLoading setLoading -> setLoading(root, setLoading.target(), setLoading.loading());
        };
    }

    private static Node replaceNode(Node node, String targetId, Node replacement) {
        if (node.id().equals(targetId)) {
            return replacement;
        }
        if (!node.hasChildren()) {
            return node;
        }
        List<Node> children = node.children().stream()
                .map(child -> replaceNode(child, targetId, replacement))
                .toList();
        return new Node(node.id(), node.type(), node.props(), children);
    }

    private static Node insertNode(Node node, String parentId, int index, Node child) {
        if (node.id().equals(parentId)) {
            List<Node> children = new ArrayList<>(node.children());
            children.add(Math.min(index, children.size()), child);
            return new Node(node.id(), node.type(), node.props(), children);
        }
        if (!node.hasChildren()) {
            return node;
        }
        List<Node> children = node.children().stream()
                .map(existing -> insertNode(existing, parentId, index, child))
                .toList();
        return new Node(node.id(), node.type(), node.props(), children);
    }

    private static Node removeNode(Node node, String targetId) {
        if (!node.hasChildren()) {
            return node;
        }
        List<Node> children = node.children().stream()
                .filter(child -> !child.id().equals(targetId))
                .map(child -> removeNode(child, targetId))
                .toList();
        return new Node(node.id(), node.type(), node.props(), children);
    }

    private static Node setErrors(Node node, String targetId, List<String> errors) {
        if (node.id().equals(targetId)) {
            Map<String, Object> props = new LinkedHashMap<>(node.props());
            if (errors.isEmpty()) {
                props.remove("errors");
            } else {
                props.put("errors", errors);
            }
            return new Node(node.id(), node.type(), props, node.children());
        }
        if (!node.hasChildren()) {
            return node;
        }
        List<Node> children = node.children().stream()
                .map(child -> setErrors(child, targetId, errors))
                .toList();
        return new Node(node.id(), node.type(), node.props(), children);
    }

    private static Node setLoading(Node node, String targetId, boolean loading) {
        if (node.id().equals(targetId)) {
            Map<String, Object> props = new LinkedHashMap<>(node.props());
            if (loading) {
                props.put("loading", true);
            } else {
                props.remove("loading");
            }
            return new Node(node.id(), node.type(), props, node.children());
        }
        if (!node.hasChildren()) {
            return node;
        }
        List<Node> children = node.children().stream()
                .map(child -> setLoading(child, targetId, loading))
                .toList();
        return new Node(node.id(), node.type(), node.props(), children);
    }

    public static Optional<Node> preview(View view, Transition transition) {
        try {
            return Optional.of(apply(view, transition).root());
        } catch (TransitionDesyncException exception) {
            return Optional.empty();
        }
    }
}
