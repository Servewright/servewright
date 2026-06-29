package io.servewright.spring;

import io.servewright.core.View;

@FunctionalInterface
public interface ViewSupplier {

    View viewForScreen(String screen);
}
