package io.servewright.spring;

import io.servewright.core.domain.Action;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import io.servewright.spring.annotation.OnAction;
import io.servewright.spring.annotation.OnAsyncValidation;
import io.servewright.spring.annotation.Payload;
import io.servewright.spring.annotation.ServewrightController;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ServewrightController
class TestSignupController {

    record SignupPayload(String email, String username) {
    }

    record UsernamePayload(String username) {
    }

    @OnAction(type = "submit", target = "signup")
    public View submit(@Payload SignupPayload payload) {
        return View.of("demo-form", Node.text("success", "Registered " + payload.email()));
    }

    @OnAsyncValidation(target = "signup", field = "username")
    public List<String> validateUsername(@Payload UsernamePayload payload) {
        if ("taken".equalsIgnoreCase(payload.username())) {
            return List.of("Username already taken");
        }
        return List.of();
    }
}
