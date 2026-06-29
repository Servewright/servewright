package io.servewright.spring.adapter.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import io.servewright.core.port.ViewSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

public final class JacksonViewSerializer implements ViewSerializer {

    private final ObjectMapper objectMapper;

    public JacksonViewSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(View view) {
        try {
            return objectMapper.writeValueAsString(toMap(view));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize Servewright view", exception);
        }
    }

    private Map<String, Object> toMap(View view) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("servewrightVersion", view.servewrightVersion());
        payload.put("schemaVersion", view.schemaVersion());
        payload.put("screen", view.screen());
        payload.put("stateVersion", view.stateVersion());
        payload.put("root", toNodeMap(view.root()));
        return payload;
    }

    private Map<String, Object> toNodeMap(Node node) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", node.id());
        payload.put("type", node.type());
        payload.put("props", new LinkedHashMap<>(node.props()));
        if (node.hasChildren()) {
            payload.put("children", node.children().stream().map(this::toNodeMap).toList());
        }
        return payload;
    }
}
