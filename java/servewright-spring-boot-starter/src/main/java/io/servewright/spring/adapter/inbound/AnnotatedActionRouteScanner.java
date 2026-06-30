package io.servewright.spring.adapter.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servewright.core.application.port.ActionHandler;
import io.servewright.core.application.port.AsyncValidationHandler;
import io.servewright.core.domain.Action;
import io.servewright.core.domain.ActionResponse;
import io.servewright.core.domain.Transition;
import io.servewright.core.domain.View;
import io.servewright.spring.annotation.OnAction;
import io.servewright.spring.annotation.OnAsyncValidation;
import io.servewright.spring.annotation.Payload;
import io.servewright.spring.annotation.ServewrightController;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class ReflectiveActionHandler implements ActionHandler {

    private final Object bean;
    private final Method method;
    private final ObjectMapper objectMapper;

    ReflectiveActionHandler(Object bean, Method method, ObjectMapper objectMapper) {
        this.bean = bean;
        this.method = method;
        this.objectMapper = objectMapper;
        this.method.setAccessible(true);
    }

    @Override
    public ActionResponse handle(Action action) {
        try {
            Object[] args = resolveArguments(action);
            Object result = method.invoke(bean, args);
            return toActionResponse(result, action);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke action handler " + method, exception);
        }
    }

    private Object[] resolveArguments(Action action) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(Payload.class)) {
                args[i] = objectMapper.convertValue(action.payload(), parameter.getType());
            } else if (Action.class.isAssignableFrom(parameter.getType())) {
                args[i] = action;
            } else {
                throw new IllegalStateException(
                        "Unsupported parameter type in action handler: " + parameter.getType().getName());
            }
        }
        return args;
    }

    private ActionResponse toActionResponse(Object result, Action action) {
        if (result instanceof ActionResponse response) {
            return response;
        }
        if (result instanceof Transition transition) {
            return ActionResponse.ofTransition(transition);
        }
        if (result instanceof View view) {
            return ActionResponse.ofView(view);
        }
        throw new IllegalStateException(
                "Action handler must return View, Transition, or ActionResponse, got: "
                        + (result == null ? "null" : result.getClass().getName()));
    }
}

final class ReflectiveAsyncValidationHandler implements AsyncValidationHandler {

    private final Object bean;
    private final Method method;
    private final ObjectMapper objectMapper;

    ReflectiveAsyncValidationHandler(Object bean, Method method, ObjectMapper objectMapper) {
        this.bean = bean;
        this.method = method;
        this.objectMapper = objectMapper;
        this.method.setAccessible(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> validate(Action action, String fieldId) {
        try {
            Object[] args = resolveArguments(action);
            Object result = method.invoke(bean, args);
            if (result instanceof List<?> list) {
                return (List<String>) list;
            }
            throw new IllegalStateException("Async validation handler must return List<String>");
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to invoke async validation handler " + method, exception);
        }
    }

    private Object[] resolveArguments(Action action) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(Payload.class)) {
                args[i] = objectMapper.convertValue(action.payload(), parameter.getType());
            } else if (Action.class.isAssignableFrom(parameter.getType())) {
                args[i] = action;
            } else if (String.class.equals(parameter.getType())) {
                args[i] = fieldIdFrom(action);
            } else {
                throw new IllegalStateException(
                        "Unsupported parameter type in async validation handler: " + parameter.getType().getName());
            }
        }
        return args;
    }

    private String fieldIdFrom(Action action) {
        return action.payload().keySet().stream().findFirst().orElse("");
    }
}

public final class AnnotatedActionRouteScanner {

    private AnnotatedActionRouteScanner() {
    }

    public static ScanResult scan(ApplicationContext applicationContext, ObjectMapper objectMapper) {
        Map<String, ActionHandlerRegistration> actionHandlers = new LinkedHashMap<>();
        Map<String, AsyncValidationHandler> asyncValidators = new LinkedHashMap<>();

        for (String beanName : applicationContext.getBeanNamesForAnnotation(ServewrightController.class)) {
            Object bean = applicationContext.getBean(beanName);
            for (Method method : bean.getClass().getMethods()) {
                OnAction onAction = method.getAnnotation(OnAction.class);
                if (onAction != null) {
                    String key = routeKey(onAction.type(), onAction.target());
                    if (actionHandlers.containsKey(key)) {
                        throw new io.servewright.core.action.DuplicateActionHandlerException(
                                onAction.type(), onAction.target());
                    }
                    actionHandlers.put(
                            key,
                            new ActionHandlerRegistration(
                                    onAction.type(),
                                    onAction.target(),
                                    new ReflectiveActionHandler(bean, method, objectMapper)));
                }

                OnAsyncValidation asyncValidation = method.getAnnotation(OnAsyncValidation.class);
                if (asyncValidation != null) {
                    if (asyncValidators.containsKey(asyncValidation.field())) {
                        throw new IllegalStateException(
                                "Duplicate async validation handler for field='" + asyncValidation.field() + "'");
                    }
                    asyncValidators.put(
                            asyncValidation.field(),
                            new ReflectiveAsyncValidationHandler(bean, method, objectMapper));
                }
            }
        }

        return new ScanResult(actionHandlers.values(), asyncValidators);
    }

    private static String routeKey(String type, String target) {
        return type + "::" + target;
    }

    public record ActionHandlerRegistration(String type, String target, ActionHandler handler) {
    }

    public record ScanResult(
            Iterable<ActionHandlerRegistration> actionHandlers,
            Map<String, AsyncValidationHandler> asyncValidators) {
    }
}
