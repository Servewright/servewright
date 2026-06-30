package io.servewright.core.transition;

import io.servewright.core.domain.Node;
import io.servewright.core.domain.Patch;
import io.servewright.core.domain.Transition;
import io.servewright.core.domain.View;
import io.servewright.core.validation.NodeTree;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TransitionBuilder {

    private final View baseline;
    private final List<Patch> patches = new ArrayList<>();

    private TransitionBuilder(View baseline) {
        this.baseline = baseline;
    }

    public static TransitionBuilder basedOn(View baseline) {
        return new TransitionBuilder(baseline);
    }

    public TransitionBuilder replace(String targetId, Node node) {
        requireNodeExists("replace", targetId);
        patches.add(new Patch.Replace(targetId, node));
        return this;
    }

    public TransitionBuilder insert(String parentId, int index, Node node) {
        requireNodeExists("insert", parentId);
        patches.add(new Patch.Insert(parentId, index, node));
        return this;
    }

    public TransitionBuilder remove(String targetId) {
        requireNodeExists("remove", targetId);
        patches.add(new Patch.Remove(targetId));
        return this;
    }

    public TransitionBuilder setError(String targetId, List<String> errors) {
        requireNodeExists("setError", targetId);
        patches.add(new Patch.SetError(targetId, List.copyOf(errors)));
        return this;
    }

    public TransitionBuilder setLoading(String targetId, boolean loading) {
        requireNodeExists("setLoading", targetId);
        patches.add(new Patch.SetLoading(targetId, loading));
        return this;
    }

    public Transition build() {
        return new Transition(baseline.stateVersion(), baseline.stateVersion() + 1, patches);
    }

    private void requireNodeExists(String op, String targetId) {
        if (NodeTree.findNodeById(baseline.root(), targetId).isEmpty()) {
            throw new UnknownPatchTargetException(op, targetId);
        }
    }
}
