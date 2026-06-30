package io.servewright.core.domain;

import java.util.List;

public sealed interface Patch permits
        Patch.Replace,
        Patch.Insert,
        Patch.Remove,
        Patch.SetError,
        Patch.SetLoading {

    String op();

    record Replace(String target, Node node) implements Patch {
        @Override
        public String op() {
            return "replace";
        }
    }

    record Insert(String parent, int index, Node node) implements Patch {
        @Override
        public String op() {
            return "insert";
        }
    }

    record Remove(String target) implements Patch {
        @Override
        public String op() {
            return "remove";
        }
    }

    record SetError(String target, List<String> errors) implements Patch {
        @Override
        public String op() {
            return "setError";
        }
    }

    record SetLoading(String target, boolean loading) implements Patch {
        @Override
        public String op() {
            return "setLoading";
        }
    }
}
