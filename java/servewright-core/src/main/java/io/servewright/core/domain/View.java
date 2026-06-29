package io.servewright.core.domain;

import java.util.Objects;

public final class View {

    public static final String DEFAULT_SERVEWRIGHT_VERSION = "1.0";
    public static final String DEFAULT_SCHEMA_VERSION = "0.1.0";

    private final String servewrightVersion;
    private final String schemaVersion;
    private final String screen;
    private final int stateVersion;
    private final Node root;

    public View(String servewrightVersion, String schemaVersion, String screen, int stateVersion, Node root) {
        this.servewrightVersion = Objects.requireNonNull(servewrightVersion, "servewrightVersion");
        this.schemaVersion = Objects.requireNonNull(schemaVersion, "schemaVersion");
        this.screen = Objects.requireNonNull(screen, "screen");
        if (stateVersion < 0) {
            throw new IllegalArgumentException("stateVersion must be >= 0");
        }
        this.stateVersion = stateVersion;
        this.root = Objects.requireNonNull(root, "root");
    }

    public static View of(String screen, Node root) {
        return new View(DEFAULT_SERVEWRIGHT_VERSION, DEFAULT_SCHEMA_VERSION, screen, 0, root);
    }

    public String servewrightVersion() {
        return servewrightVersion;
    }

    public String schemaVersion() {
        return schemaVersion;
    }

    public String screen() {
        return screen;
    }

    public int stateVersion() {
        return stateVersion;
    }

    public Node root() {
        return root;
    }
}
