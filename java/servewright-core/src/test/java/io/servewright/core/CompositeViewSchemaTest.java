package io.servewright.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.StandardNodes;
import io.servewright.core.domain.View;
import io.servewright.core.infrastructure.JsonViewSerializer;
import io.servewright.core.port.ViewSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompositeViewSchemaTest {

    private static final ViewSerializer SERIALIZER = new JsonViewSerializer();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static JsonSchema viewSchema;
    private static JsonSchema formSchema;
    private static JsonSchema textInputSchema;

    @BeforeAll
    static void loadSchemas() throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        viewSchema = factory.getSchema(readResource("/spec/protocol/view.schema.json"));
        formSchema = factory.getSchema(readResource("/spec/primitives/form.schema.json"));
        textInputSchema = factory.getSchema(readResource("/spec/primitives/text-input.schema.json"));
    }

    @Test
    void compositeFormTreeValidatesAgainstSchemas() throws Exception {
        Node root = StandardNodes.form(
                "signup-form",
                "signup",
                List.of(
                        StandardNodes.group(
                                "personal-group",
                                "Personal",
                                List.of(
                                        StandardNodes.textInput(
                                                "email",
                                                "Email",
                                                "",
                                                "you@example.com",
                                                true),
                                        StandardNodes.button("submit", "Submit", "submit")))));

        View view = View.of("demo-form", root);
        String json = SERIALIZER.serialize(view);
        JsonNode document = MAPPER.readTree(json);

        assertTrue(viewSchema.validate(document).isEmpty());

        JsonNode formNode = document.get("root");
        assertTrue(formSchema.validate(formNode).isEmpty());

        JsonNode textInputNode = formNode.get("children").get(0).get("children").get(0);
        Set<ValidationMessage> textInputErrors = textInputSchema.validate(textInputNode);
        assertTrue(textInputErrors.isEmpty(), () -> "text-input schema errors: " + textInputErrors);
    }

    @Test
    void invalidTextInputPropFailsSchemaValidation() throws Exception {
        String invalidJson = """
                {"id":"email","type":"TextInput","props":{"label":"Email","required":"yes"}}""";
        JsonNode textInputNode = MAPPER.readTree(invalidJson);

        Set<ValidationMessage> errors = textInputSchema.validate(textInputNode);
        assertFalse(errors.isEmpty());
    }

    private static InputStream readResource(String path) throws Exception {
        var url = CompositeViewSchemaTest.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing test resource: " + path);
        }
        return url.openStream();
    }
}
