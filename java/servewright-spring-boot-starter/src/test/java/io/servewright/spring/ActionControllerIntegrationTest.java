package io.servewright.spring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        assertTrue(document.get("view").get("root").get("props").get("content").asText().startsWith("Registered"));
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

        JsonNode errors = objectMapper.readTree(response)
                .get("view")
                .get("root")
                .get("children")
                .get(0)
                .get("children")
                .get(0)
                .get("props")
                .get("errors");

        assertTrue(errors.isArray() && errors.size() > 0);
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

        JsonNode errors = objectMapper.readTree(response)
                .get("view")
                .get("root")
                .get("children")
                .get(0)
                .get("children")
                .get(1)
                .get("props")
                .get("errors");

        assertTrue(errors.isArray() && errors.size() > 0);
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
