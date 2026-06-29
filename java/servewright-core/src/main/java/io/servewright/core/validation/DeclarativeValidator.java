package io.servewright.core.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DeclarativeValidator {

    private DeclarativeValidator() {
    }

    public static List<String> validateTextInput(Map<String, Object> props, String value) {
        List<String> errors = new ArrayList<>();
        String normalized = value == null ? "" : value;

        if (Boolean.TRUE.equals(props.get("required")) && normalized.isBlank()) {
            errors.add("Required");
        }

        Object minLength = props.get("minLength");
        if (minLength instanceof Number number && normalized.length() < number.intValue()) {
            errors.add("Minimum length is " + number.intValue());
        }

        Object pattern = props.get("pattern");
        if (pattern instanceof String regex && !normalized.isBlank() && !normalized.matches(regex)) {
            errors.add("Invalid format");
        }

        return errors;
    }
}
