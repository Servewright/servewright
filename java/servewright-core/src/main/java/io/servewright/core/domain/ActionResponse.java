package io.servewright.core.domain;

public record ActionResponse(View view, Transition transition) {

    public static ActionResponse ofView(View view) {
        return new ActionResponse(view, null);
    }

    public static ActionResponse ofTransition(Transition transition) {
        return new ActionResponse(null, transition);
    }

    public boolean hasTransition() {
        return transition != null;
    }

    public boolean hasView() {
        return view != null;
    }
}
