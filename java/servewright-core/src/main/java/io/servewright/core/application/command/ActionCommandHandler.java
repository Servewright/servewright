package io.servewright.core.application.command;

import io.servewright.core.action.ActionRouter;
import io.servewright.core.application.ViewState;
import io.servewright.core.application.port.ActionHandler;
import io.servewright.core.application.port.AsyncValidationHandler;
import io.servewright.core.application.port.TransitionPublisher;
import io.servewright.core.application.port.ViewResolver;
import io.servewright.core.domain.Action;
import io.servewright.core.domain.ActionResponse;
import io.servewright.core.domain.Node;
import io.servewright.core.domain.Transition;
import io.servewright.core.domain.View;
import io.servewright.core.transition.TransitionBuilder;
import io.servewright.core.validation.FieldValidator;
import io.servewright.core.validation.NodeTree;

import java.util.List;
import java.util.Map;

public final class ActionCommandHandler {

    private final ActionRouter actionRouter;
    private final ViewResolver viewResolver;
    private final ViewState viewState;
    private final TransitionPublisher transitionPublisher;
    private final Map<String, AsyncValidationHandler> asyncValidators;

    public ActionCommandHandler(
            ActionRouter actionRouter,
            ViewResolver viewResolver,
            ViewState viewState,
            TransitionPublisher transitionPublisher,
            Map<String, AsyncValidationHandler> asyncValidators) {
        this.actionRouter = actionRouter;
        this.viewResolver = viewResolver;
        this.viewState = viewState;
        this.transitionPublisher = transitionPublisher;
        this.asyncValidators = Map.copyOf(asyncValidators);
    }

    public ActionCommandHandler(
            ActionRouter actionRouter,
            ViewResolver viewResolver,
            ViewState viewState,
            TransitionPublisher transitionPublisher) {
        this(actionRouter, viewResolver, viewState, transitionPublisher, Map.of());
    }

    public ActionResponse handle(Action action) {
        View baseline = viewState.getOrLoad(action.screen());
        if (baseline.stateVersion() != action.stateVersion()) {
            return ActionResponse.ofView(viewResolver.resolve(action.screen()));
        }

        Node formRoot = NodeTree.findFormByActionTarget(baseline.root(), action.target())
                .orElse(baseline.root());

        if ("asyncValidate".equals(action.type())) {
            return handleAsyncValidation(action, baseline);
        }

        Map<String, List<String>> errors = FieldValidator.validateForm(formRoot, action.payload());
        if (!errors.isEmpty()) {
            return publishTransition(action.screen(), validationTransition(baseline, errors));
        }

        ActionHandler handler = actionRouter.route(action.type(), action.target())
                .orElseThrow(() -> new UnknownActionException(action.type(), action.target()));

        return publishTransition(action.screen(), toTransition(baseline, handler.handle(action)));
    }

    private ActionResponse handleAsyncValidation(Action action, View baseline) {
        String fieldId = action.payload().keySet().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("asyncValidate payload must contain a field id"));

        AsyncValidationHandler validator = asyncValidators.get(fieldId);
        if (validator == null) {
            throw new UnknownAsyncValidationException(fieldId);
        }

        List<String> errors = validator.validate(action, fieldId);
        TransitionBuilder builder = TransitionBuilder.basedOn(baseline);
        if (errors.isEmpty()) {
            builder.setError(fieldId, List.of());
        } else {
            builder.setError(fieldId, errors);
        }
        return publishTransition(action.screen(), builder.build());
    }

    private Transition validationTransition(View baseline, Map<String, List<String>> errors) {
        TransitionBuilder builder = TransitionBuilder.basedOn(baseline);
        for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
            builder.setError(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    private Transition toTransition(View baseline, ActionResponse response) {
        if (response.hasTransition()) {
            return response.transition();
        }
        View resultView = response.view();
        return TransitionBuilder.basedOn(baseline)
                .replace(baseline.root().id(), resultView.root())
                .build();
    }

    private ActionResponse publishTransition(String screen, Transition transition) {
        viewState.applyTransition(screen, transition);
        transitionPublisher.publish(screen, transition);
        return ActionResponse.ofTransition(transition);
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
