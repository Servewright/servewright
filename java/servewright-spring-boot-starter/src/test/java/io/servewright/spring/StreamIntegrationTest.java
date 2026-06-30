package io.servewright.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = StreamIntegrationTest.TestApplication.class)
@AutoConfigureMockMvc
class StreamIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void viewEndpointReturnsEtag() throws Exception {
        mockMvc.perform(get("/servewright/view/demo-form"))
                .andExpect(status().isOk())
                .andExpect(header().exists("ETag"));
    }

    @Test
    void streamEndpointIsAvailable() throws Exception {
        mockMvc.perform(get("/servewright/stream/demo-form").accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk());
    }

    @Test
    void actionPublishesTransitionWithIncreasedStateVersion() throws Exception {
        mockMvc.perform(get("/servewright/view/demo-form")).andExpect(status().isOk());

        String body = """
                {
                  "type":"submit",
                  "target":"signup",
                  "screen":"demo-form",
                  "stateVersion":0,
                  "payload":{"email":"user@example.com","username":"validuser"}
                }
                """;

        mockMvc.perform(post("/servewright/action")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(get("/servewright/view/demo-form"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"demo-form:0.1.0:1\""));
    }

    @SpringBootApplication
    static class TestApplication {
    }
}
