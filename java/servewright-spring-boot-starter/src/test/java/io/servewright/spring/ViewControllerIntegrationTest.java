package io.servewright.spring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.InputStream;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ViewControllerIntegrationTest.TestApplication.class)
@AutoConfigureMockMvc
class ViewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static JsonSchema viewSchema;
    private static JsonSchema textSchema;

    @BeforeAll
    static void loadSchemas() throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        viewSchema = factory.getSchema(readResource("/spec/protocol/view.schema.json"));
        textSchema = factory.getSchema(readResource("/spec/primitives/text.schema.json"));
    }

    @Test
    void getHelloViewReturnsSchemaConformantJson() throws Exception {
        String body = mockMvc.perform(get("/servewright/view/hello"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode document = objectMapper.readTree(body);

        Set<ValidationMessage> viewErrors = viewSchema.validate(document);
        assertTrue(viewErrors.isEmpty(), () -> "view schema errors: " + viewErrors);

        Set<ValidationMessage> textErrors = textSchema.validate(document.get("root"));
        assertTrue(textErrors.isEmpty(), () -> "text schema errors: " + textErrors);
    }

    private static InputStream readResource(String path) throws Exception {
        var url = ViewControllerIntegrationTest.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing test resource: " + path);
        }
        return url.openStream();
    }

    @SpringBootApplication
    static class TestApplication {
    }
}
