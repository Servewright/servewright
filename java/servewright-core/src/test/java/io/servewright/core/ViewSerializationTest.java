package io.servewright.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewSerializationTest {

    private static final Serializer SERIALIZER = new JsonSerializer();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonSchema viewSchema;
    private static JsonSchema textSchema;

    @BeforeAll
    static void loadSchemas() throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        viewSchema = factory.getSchema(readResource("/spec/protocol/view.schema.json"));
        textSchema = factory.getSchema(readResource("/spec/primitives/text.schema.json"));
    }

    @Test
    void serializesHelloViewToExpectedJson() {
        View view = View.of("hello", Node.text("greeting", "Bonjour"));

        String json = SERIALIZER.serialize(view);

        String expected = """
                {"servewrightVersion":"1.0","schemaVersion":"0.1.0","screen":"hello","stateVersion":0,"root":{"id":"greeting","type":"Text","props":{"content":"Bonjour"}}}""";
        assertEquals(expected, json);
    }

    @Test
    void serializedJsonValidatesAgainstViewAndTextSchemas() throws Exception {
        View view = View.of("hello", Node.text("greeting", "Bonjour"));
        String json = SERIALIZER.serialize(view);
        JsonNode document = MAPPER.readTree(json);

        Set<ValidationMessage> viewErrors = viewSchema.validate(document);
        assertTrue(viewErrors.isEmpty(), () -> "view schema errors: " + viewErrors);

        Set<ValidationMessage> textErrors = textSchema.validate(document.get("root"));
        assertTrue(textErrors.isEmpty(), () -> "text schema errors: " + textErrors);
    }

    private static InputStream readResource(String path) throws Exception {
        var url = ViewSerializationTest.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing test resource: " + path);
        }
        return url.openStream();
    }
}
