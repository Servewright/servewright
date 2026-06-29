package io.servewright.core.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Node {

    private final String id;
    private final String type;
    private final Map<String, Object> props;
    private final List<Node> children;

    public Node(String id, String type, Map<String, Object> props, List<Node> children) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.props = Map.copyOf(Objects.requireNonNull(props, "props"));
        this.children = children == null || children.isEmpty()
                ? List.of()
                : List.copyOf(children);
    }

    public static Node text(String id, String content) {
        return text(id, content, "body");
    }

    public static Node text(String id, String content, String emphasis) {
        Map<String, Object> props = new LinkedHashMap<>();
        props.put("content", content);
        if (!"body".equals(emphasis)) {
            props.put("emphasis", emphasis);
        }
        return new Node(id, "Text", props, List.of());
    }

    public String id() {
        return id;
    }

    public String type() {
        return type;
    }

    public Map<String, Object> props() {
        return props;
    }

    public List<Node> children() {
        return children;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }
}
