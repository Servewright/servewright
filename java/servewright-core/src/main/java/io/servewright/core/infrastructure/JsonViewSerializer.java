package io.servewright.core.infrastructure;

import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import io.servewright.core.port.ViewSerializer;

import java.util.List;
import java.util.Map;

public final class JsonViewSerializer implements ViewSerializer {

    @Override
    public String serialize(View view) {
        StringBuilder json = new StringBuilder(256);
        json.append('{');
        appendField(json, "servewrightVersion", view.servewrightVersion());
        json.append(',');
        appendField(json, "schemaVersion", view.schemaVersion());
        json.append(',');
        appendField(json, "screen", view.screen());
        json.append(',');
        json.append("\"stateVersion\":").append(view.stateVersion()).append(',');
        json.append("\"root\":");
        appendNode(json, view.root());
        json.append('}');
        return json.toString();
    }

    private void appendNode(StringBuilder json, Node node) {
        json.append('{');
        appendField(json, "id", node.id());
        json.append(',');
        appendField(json, "type", node.type());
        json.append(',');
        json.append("\"props\":");
        appendProps(json, node.props());
        if (node.hasChildren()) {
            json.append(",\"children\":[");
            for (int i = 0; i < node.children().size(); i++) {
                if (i > 0) {
                    json.append(',');
                }
                appendNode(json, node.children().get(i));
            }
            json.append(']');
        }
        json.append('}');
    }

    private void appendProps(StringBuilder json, Map<String, Object> props) {
        json.append('{');
        int index = 0;
        for (Map.Entry<String, Object> entry : props.entrySet()) {
            if (index++ > 0) {
                json.append(',');
            }
            json.append('"').append(escape(entry.getKey())).append("\":");
            appendValue(json, entry.getValue());
        }
        json.append('}');
    }

    private void appendValue(StringBuilder json, Object value) {
        switch (value) {
            case null -> json.append("null");
            case String string -> json.append('"').append(escape(string)).append('"');
            case Boolean booleanValue -> json.append(booleanValue);
            case Number number -> json.append(number);
            case List<?> list -> appendList(json, list);
            case Map<?, ?> map -> appendMap(json, map);
            default -> json.append('"').append(escape(String.valueOf(value))).append('"');
        }
    }

    private void appendList(StringBuilder json, List<?> list) {
        json.append('[');
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            appendValue(json, list.get(i));
        }
        json.append(']');
    }

    private void appendMap(StringBuilder json, Map<?, ?> map) {
        json.append('{');
        int index = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (index++ > 0) {
                json.append(',');
            }
            json.append('"').append(escape(String.valueOf(entry.getKey()))).append("\":");
            appendValue(json, entry.getValue());
        }
        json.append('}');
    }

    private void appendField(StringBuilder json, String name, String value) {
        json.append('"').append(escape(name)).append("\":");
        json.append('"').append(escape(value)).append('"');
    }

    private String escape(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
