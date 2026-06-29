package io.servewright.spring;

import io.servewright.core.action.DuplicateActionHandlerException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import io.servewright.core.domain.View;
import io.servewright.core.domain.Node;
import io.servewright.spring.annotation.OnAction;
import io.servewright.spring.annotation.ServewrightController;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DuplicateActionHandlerIntegrationTest {

    @Test
    void duplicateOnActionHandlersFailAtStartup() {
        try {
            SpringApplication.run(
                    TestApplication.class,
                    "--spring.main.web-application-type=none");
            throw new AssertionError("Expected startup failure");
        } catch (Exception exception) {
            assertTrue(hasDuplicateCause(exception));
        }
    }

    private static boolean hasDuplicateCause(Throwable exception) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof DuplicateActionHandlerException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    @Component
    @ServewrightController
    static class DuplicateA {
        @OnAction(type = "submit", target = "signup")
        public View handleA() {
            return View.of("demo-form", Node.text("a", "a"));
        }
    }

    @Component
    @ServewrightController
    static class DuplicateB {
        @OnAction(type = "submit", target = "signup")
        public View handleB() {
            return View.of("demo-form", Node.text("b", "b"));
        }
    }

    @SpringBootApplication
    static class TestApplication {
        @Bean
        DuplicateA duplicateA() {
            return new DuplicateA();
        }

        @Bean
        DuplicateB duplicateB() {
            return new DuplicateB();
        }
    }
}
