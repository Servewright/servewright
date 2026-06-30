package io.servewright.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servewright.core.action.ActionRouter;
import io.servewright.core.application.ViewState;
import io.servewright.core.application.command.ActionCommandHandler;
import io.servewright.core.application.port.ViewResolver;
import io.servewright.core.port.TransitionSerializer;
import io.servewright.core.port.ViewSerializer;
import io.servewright.spring.adapter.inbound.ActionRouteLogger;
import io.servewright.spring.adapter.inbound.AnnotatedActionRouteScanner;
import io.servewright.spring.adapter.inbound.web.ServewrightActionEndpoint;
import io.servewright.spring.adapter.inbound.web.ServewrightStreamEndpoint;
import io.servewright.spring.adapter.inbound.web.ServewrightViewEndpoint;
import io.servewright.spring.adapter.outbound.DefaultViewResolver;
import io.servewright.spring.adapter.outbound.JacksonTransitionSerializer;
import io.servewright.spring.adapter.outbound.JacksonViewSerializer;
import io.servewright.spring.adapter.outbound.SseTransitionPublisher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = {
        ServewrightViewEndpoint.class,
        ServewrightActionEndpoint.class,
        ServewrightStreamEndpoint.class
})
public class ServewrightAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ViewSerializer servewrightViewSerializer(ObjectMapper objectMapper) {
        return new JacksonViewSerializer(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public TransitionSerializer servewrightTransitionSerializer() {
        return new JacksonTransitionSerializer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ViewResolver servewrightViewResolver() {
        return new DefaultViewResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public ViewState servewrightViewState(ViewResolver viewResolver) {
        return new ViewState(viewResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public io.servewright.core.application.query.ViewQueryHandler servewrightViewQueryHandler(
            ViewResolver viewResolver) {
        return new io.servewright.core.application.query.ViewQueryHandler(viewResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public io.servewright.core.primitive.PrimitiveRegistry servewrightPrimitiveRegistry() {
        return io.servewright.core.primitive.PrimitiveRegistry.withStandardPrimitives();
    }

    @Bean
    @ConditionalOnMissingBean
    public ActionRouter servewrightActionRouter(ApplicationContext applicationContext, ObjectMapper objectMapper) {
        ActionRouter.Builder builder = ActionRouter.builder();
        AnnotatedActionRouteScanner.ScanResult scanResult =
                AnnotatedActionRouteScanner.scan(applicationContext, objectMapper);
        for (AnnotatedActionRouteScanner.ActionHandlerRegistration registration : scanResult.actionHandlers()) {
            builder.on(registration.type(), registration.target(), registration.handler());
        }
        ActionRouter router = builder.build();
        ActionRouteLogger.logDiscoveredRoutes(applicationContext, router);
        return router;
    }

    @Bean
    @ConditionalOnMissingBean
    public SseTransitionPublisher servewrightSseTransitionPublisher(TransitionSerializer transitionSerializer) {
        return new SseTransitionPublisher(transitionSerializer);
    }

    @Bean
    @ConditionalOnMissingBean
    public ActionCommandHandler servewrightActionCommandHandler(
            ActionRouter actionRouter,
            ViewResolver viewResolver,
            ViewState viewState,
            SseTransitionPublisher transitionPublisher,
            ApplicationContext applicationContext,
            ObjectMapper objectMapper) {
        AnnotatedActionRouteScanner.ScanResult scanResult =
                AnnotatedActionRouteScanner.scan(applicationContext, objectMapper);
        return new ActionCommandHandler(
                actionRouter,
                viewResolver,
                viewState,
                transitionPublisher,
                scanResult.asyncValidators());
    }
}
