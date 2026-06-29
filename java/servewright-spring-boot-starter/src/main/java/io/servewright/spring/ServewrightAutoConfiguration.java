package io.servewright.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.servewright.core.application.port.ViewResolver;
import io.servewright.core.application.query.ViewQueryHandler;
import io.servewright.core.port.ViewSerializer;
import io.servewright.spring.adapter.inbound.web.ServewrightViewEndpoint;
import io.servewright.spring.adapter.outbound.DefaultViewResolver;
import io.servewright.spring.adapter.outbound.JacksonViewSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = ServewrightViewEndpoint.class)
public class ServewrightAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ViewSerializer servewrightViewSerializer(ObjectMapper objectMapper) {
        return new JacksonViewSerializer(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ViewResolver servewrightViewResolver() {
        return new DefaultViewResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public ViewQueryHandler servewrightViewQueryHandler(ViewResolver viewResolver) {
        return new ViewQueryHandler(viewResolver);
    }
}
