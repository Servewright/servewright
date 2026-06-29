package io.servewright.core;

import io.servewright.core.domain.StandardNodes;
import io.servewright.core.validation.DeclarativeValidator;
import io.servewright.core.validation.FieldValidator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeclarativeValidatorTest {

    @Test
    void requiredFieldRejectsBlankValue() {
        var node = StandardNodes.textInput("email", "Email", "", null, true);
        List<String> errors = DeclarativeValidator.validateTextInput(node.props(), "");
        assertFalse(errors.isEmpty());
    }

    @Test
    void patternRejectsInvalidEmail() {
        var node = StandardNodes.textInput(
                "email", "Email", "", null, true, null, "^[^@]+@[^@]+\\.[^@]+$", false, null);
        List<String> errors = DeclarativeValidator.validateTextInput(node.props(), "not-an-email");
        assertFalse(errors.isEmpty());
    }

    @Test
    void validEmailPassesValidation() {
        var node = StandardNodes.textInput(
                "email", "Email", "", null, true, null, "^[^@]+@[^@]+\\.[^@]+$", false, null);
        List<String> errors = DeclarativeValidator.validateTextInput(node.props(), "user@example.com");
        assertTrue(errors.isEmpty());
    }

    @Test
    void minLengthRejectsShortValue() {
        var node = StandardNodes.textInput("username", "Username", "", null, true, 3, null, false, null);
        List<String> errors = DeclarativeValidator.validateTextInput(node.props(), "ab");
        assertFalse(errors.isEmpty());
    }

    @Test
    void formValidationCollectsErrorsByFieldId() {
        var form = StandardNodes.form(
                "form",
                "signup",
                List.of(StandardNodes.textInput(
                        "email", "Email", "", null, true, null, "^[^@]+@[^@]+\\.[^@]+$", false, null)));

        var errors = FieldValidator.validateForm(form, Map.of("email", "bad"));
        assertTrue(errors.containsKey("email"));
    }
}
