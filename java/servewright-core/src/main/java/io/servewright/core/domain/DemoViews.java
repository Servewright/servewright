package io.servewright.core.domain;

import java.util.List;

public final class DemoViews {

    private DemoViews() {
    }

    public static View hello(String screen) {
        return View.of(screen, Node.text("greeting", "Bonjour"));
    }

    public static View signupForm(String screen) {
        return View.of(screen, signupFormRoot());
    }

    public static Node signupFormRoot() {
        return StandardNodes.form(
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
                                                true,
                                                null,
                                                "^[^@]+@[^@]+\\.[^@]+$",
                                                false,
                                                "onBlur"),
                                        StandardNodes.textInput(
                                                "username",
                                                "Username",
                                                "",
                                                "Choose a username",
                                                true,
                                                3,
                                                null,
                                                true,
                                                "onBlur"),
                                        StandardNodes.button("submit", "Submit", "submit")))));
    }
}
