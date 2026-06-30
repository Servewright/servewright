package io.servewright.spring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.servewright.core.application.ViewState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ActionControllerIntegrationTest.TestApplication.class)
@AutoConfigureMockMvc
class ActionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ViewState viewState;

    @BeforeEach
    void resetViewState() {
        viewState.reset("demo-form");
    }

    @Test
    void submitWithValidPayloadExecutesHandler() throws Exception {
        String body = """
                {
                  "type":"submit",
                  "target":"signup",
                  "screen":"demo-form",
                  "stateVersion":0,
                  "payload":{"email":"user@example.com","username":"validuser"}
                }
                """;

        String response = mockMvc.perform(post("/servewright/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode document = objectMapper.readTree(response);
        JsonNode transition = document.get("transition");
        assertTrue(transition.has("stateVersion"));
        assertTrue(transition.get("stateVersion").asInt() > 0);
    }

    @Test
    void submitWithInvalidEmailIsRejectedByServerValidation() throws Exception {
        String body = """
                {
                  "type":"submit",
                  "target":"signup",
                  "screen":"demo-form",
                  "stateVersion":0,
                  "payload":{"email":"bad","username":"validuser"}
                }
                """;

        String response = mockMvc.perform(post("/servewright/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode patches = objectMapper.readTree(response).get("transition").get("patches");
        assertTrue(patches.isArray() && patches.size() > 0);
        assertTrue("setError".equals(patches.get(0).get("op").asText()));
    }

    @Test
    void asyncValidationRejectsTakenUsername() throws Exception {
        String body = """
                {
                  "type":"asyncValidate",
                  "target":"signup",
                  "screen":"demo-form",
                  "stateVersion":0,
                  "payload":{"username":"taken"}
                }
                """;

        String response = mockMvc.perform(post("/servewright/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode patches = objectMapper.readTree(response).get("transition").get("patches");
        assertTrue(patches.isArray() && patches.size() > 0);
    }

    @Test
    void malformedActionRequestReturnsClientError() throws Exception {
        mockMvc.perform(post("/servewright/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @SpringBootApplication
    static class TestApplication {
    }
}
