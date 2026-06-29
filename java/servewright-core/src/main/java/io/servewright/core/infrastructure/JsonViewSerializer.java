package io.servewright.core.infrastructure;

import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import io.servewright.core.port.ViewSerializer;

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
            appendField(json, entry.getKey(), String.valueOf(entry.getValue()));
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
