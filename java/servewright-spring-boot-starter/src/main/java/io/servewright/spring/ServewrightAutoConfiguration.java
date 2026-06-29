package io.servewright.spring;

import io.servewright.core.Node;
import io.servewright.core.Serializer;
import io.servewright.core.View;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.databind.ObjectMapper;

@AutoConfiguration
@ComponentScan(basePackageClasses = ViewController.class)
public class ServewrightAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Serializer servewrightSerializer(ObjectMapper objectMapper) {
        return new JacksonViewSerializer(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ViewSupplier servewrightViewSupplier() {
        return screen -> View.of(screen, Node.text("greeting", "Bonjour"));
    }
}
