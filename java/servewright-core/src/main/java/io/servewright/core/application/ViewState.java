package io.servewright.core.application;

import io.servewright.core.application.port.ViewResolver;
import io.servewright.core.domain.Transition;
import io.servewright.core.domain.View;
import io.servewright.core.transition.PatchApplier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ViewState {

    private final ViewResolver viewResolver;
    private final Map<String, View> views = new ConcurrentHashMap<>();

    public ViewState(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    public View getOrLoad(String screen) {
        return views.computeIfAbsent(screen, viewResolver::resolve);
    }

    public View applyTransition(String screen, Transition transition) {
        View current = getOrLoad(screen);
        View updated = PatchApplier.apply(current, transition);
        views.put(screen, updated);
        return updated;
    }

    public void reset(String screen) {
        views.remove(screen);
    }

    public View replace(String screen, View view) {
        views.put(screen, view);
        return view;
    }
}
