package io.servewright.spring.adapter.inbound.web;

import io.servewright.core.application.query.GetViewQuery;
import io.servewright.core.application.query.ViewQueryHandler;
import io.servewright.core.port.ViewSerializer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servewright/view")
public class ServewrightViewEndpoint {

    private final ViewQueryHandler viewQueryHandler;
    private final ViewSerializer viewSerializer;

    public ServewrightViewEndpoint(ViewQueryHandler viewQueryHandler, ViewSerializer viewSerializer) {
        this.viewQueryHandler = viewQueryHandler;
        this.viewSerializer = viewSerializer;
    }

    @GetMapping(value = "/{screen}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getView(@PathVariable("screen") String screen) {
        return viewSerializer.serialize(viewQueryHandler.handle(new GetViewQuery(screen)));
    }
}
