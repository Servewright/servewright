package io.servewright.core.infrastructure;

import io.servewright.core.domain.Node;
import io.servewright.core.domain.Patch;
import io.servewright.core.domain.Transition;
import io.servewright.core.port.TransitionSerializer;

import java.util.List;
import java.util.Map;

public final class JsonTransitionSerializer implements TransitionSerializer {

    @Override
    public String serialize(Transition transition) {
        StringBuilder json = new StringBuilder(256);
        json.append('{');
        appendField(json, "basedOn", transition.basedOn());
        json.append(',');
        appendField(json, "stateVersion", transition.stateVersion());
        json.append(",\"patches\":[");
        for (int i = 0; i < transition.patches().size(); i++) {
            if (i > 0) {
                json.append(',');
            }
            appendPatch(json, transition.patches().get(i));
        }
        json.append("]}");
        return json.toString();
    }

    private void appendPatch(StringBuilder json, Patch patch) {
        json.append('{');
        appendStringField(json, "op", patch.op());
        switch (patch) {
            case Patch.Replace replace -> {
                json.append(',');
                appendStringField(json, "target", replace.target());
                json.append(",\"node\":");
                appendNode(json, replace.node());
            }
            case Patch.Insert insert -> {
                json.append(',');
                appendStringField(json, "parent", insert.parent());
                json.append(',');
                appendField(json, "index", insert.index());
                json.append(",\"node\":");
                appendNode(json, insert.node());
            }
            case Patch.Remove remove -> {
                json.append(',');
                appendStringField(json, "target", remove.target());
            }
            case Patch.SetError setError -> {
                json.append(',');
                appendStringField(json, "target", setError.target());
                json.append(",\"errors\":[");
                for (int i = 0; i < setError.errors().size(); i++) {
                    if (i > 0) {
                        json.append(',');
                    }
                    json.append('"').append(escape(setError.errors().get(i))).append('"');
                }
                json.append(']');
            }
            case Patch.SetLoading setLoading -> {
                json.append(',');
                appendStringField(json, "target", setLoading.target());
                json.append(',');
                json.append("\"loading\":").append(setLoading.loading());
            }
        }
        json.append('}');
    }

    private void appendNode(StringBuilder json, Node node) {
        json.append('{');
        appendStringField(json, "id", node.id());
        json.append(',');
        appendStringField(json, "type", node.type());
        json.append(",\"props\":");
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
            case List<?> list -> {
                json.append('[');
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        json.append(',');
                    }
                    appendValue(json, list.get(i));
                }
                json.append(']');
            }
            case Map<?, ?> map -> {
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
            default -> json.append('"').append(escape(String.valueOf(value))).append('"');
        }
    }

    private void appendField(StringBuilder json, String name, int value) {
        json.append('"').append(name).append("\":").append(value);
    }

    private void appendStringField(StringBuilder json, String name, String value) {
        json.append('"').append(name).append("\":\"").append(escape(value)).append('"');
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
