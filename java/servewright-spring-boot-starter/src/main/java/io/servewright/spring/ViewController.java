package io.servewright.spring;

import io.servewright.core.Serializer;
import io.servewright.core.View;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
@RequestMapping("/servewright/view")
public class ViewController {

    private final Serializer serializer;
    private final ViewSupplier viewSupplier;

    public ViewController(Serializer serializer, ViewSupplier viewSupplier) {
        this.serializer = serializer;
        this.viewSupplier = viewSupplier;
    }

    @GetMapping(value = "/{screen}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getView(@PathVariable("screen") String screen) {
        View view = viewSupplier.viewForScreen(screen);
        return serializer.serialize(view);
    }
}
