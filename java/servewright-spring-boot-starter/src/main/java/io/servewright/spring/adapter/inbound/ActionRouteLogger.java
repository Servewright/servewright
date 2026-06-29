package io.servewright.spring.adapter.inbound;

import io.servewright.core.action.ActionRoute;
import io.servewright.core.action.ActionRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public final class ActionRouteLogger {

    private static final Logger log = LoggerFactory.getLogger(ActionRouteLogger.class);

    private ActionRouteLogger() {
    }

    public static void logDiscoveredRoutes(ApplicationContext applicationContext, ActionRouter actionRouter) {
        for (ActionRoute route : actionRouter.routes()) {
            log.info("Registered Servewright action route: type='{}' target='{}'", route.type(), route.target());
        }

        String[] controllers = applicationContext.getBeanNamesForAnnotation(
                io.servewright.spring.annotation.ServewrightController.class);
        if (controllers.length == 0) {
            log.info("No @ServewrightController beans found");
        }
    }
}
