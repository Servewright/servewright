package io.servewright.core.application.command;

import io.servewright.core.action.ActionRouter;
import io.servewright.core.application.port.ActionHandler;
import io.servewright.core.application.port.AsyncValidationHandler;
import io.servewright.core.application.port.ViewResolver;
import io.servewright.core.domain.Action;
import io.servewright.core.domain.ActionResponse;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.View;
import io.servewright.core.validation.FieldValidator;
import io.servewright.core.validation.NodeTree;

import java.util.List;
import java.util.Map;

public final class ActionCommandHandler {

    private final ActionRouter actionRouter;
    private final ViewResolver viewResolver;
    private final Map<String, AsyncValidationHandler> asyncValidators;

    public ActionCommandHandler(
            ActionRouter actionRouter,
            ViewResolver viewResolver,
            Map<String, AsyncValidationHandler> asyncValidators) {
        this.actionRouter = actionRouter;
        this.viewResolver = viewResolver;
        this.asyncValidators = Map.copyOf(asyncValidators);
    }

    public ActionCommandHandler(ActionRouter actionRouter, ViewResolver viewResolver) {
        this(actionRouter, viewResolver, Map.of());
    }

    public ActionResponse handle(Action action) {
        View baseline = viewResolver.resolve(action.screen());
        Node formRoot = NodeTree.findFormByActionTarget(baseline.root(), action.target())
                .orElse(baseline.root());

        if ("asyncValidate".equals(action.type())) {
            return handleAsyncValidation(action, baseline, formRoot);
        }

        Map<String, List<String>> errors = FieldValidator.validateForm(formRoot, action.payload());
        if (!errors.isEmpty()) {
            return validationErrorResponse(baseline, action.payload(), errors);
        }

        ActionHandler handler = actionRouter.route(action.type(), action.target())
                .orElseThrow(() -> new UnknownActionException(action.type(), action.target()));

        return handler.handle(action);
    }

    private ActionResponse handleAsyncValidation(Action action, View baseline, Node formRoot) {
        String fieldId = action.payload().keySet().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("asyncValidate payload must contain a field id"));

        AsyncValidationHandler validator = asyncValidators.get(fieldId);
        if (validator == null) {
            throw new UnknownAsyncValidationException(fieldId);
        }

        List<String> errors = validator.validate(action, fieldId);
        Map<String, List<String>> errorMap = errors.isEmpty()
                ? Map.of()
                : Map.of(fieldId, errors);

        return validationErrorResponse(baseline, action.payload(), errorMap);
    }

    private ActionResponse validationErrorResponse(
            View baseline,
            Map<String, Object> values,
            Map<String, List<String>> errors) {
        Node updatedRoot = NodeTree.applyFieldStates(baseline.root(), values, errors, Map.of());
        View view = baseline.withRoot(updatedRoot).withIncrementedStateVersion();
        return new ActionResponse(view);
    }

    public static final class UnknownActionException extends RuntimeException {
        public UnknownActionException(String type, String target) {
            super("No action handler for type='" + type + "' target='" + target + "'");
        }
    }

    public static final class UnknownAsyncValidationException extends RuntimeException {
        public UnknownAsyncValidationException(String fieldId) {
            super("No async validation handler for field='" + fieldId + "'");
        }
    }
}
