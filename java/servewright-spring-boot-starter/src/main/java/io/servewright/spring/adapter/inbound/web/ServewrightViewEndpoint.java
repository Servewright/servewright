package io.servewright.spring.adapter.inbound.web;

import io.servewright.core.application.ViewState;
import io.servewright.core.domain.View;
import io.servewright.core.port.ViewSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servewright/view")
public class ServewrightViewEndpoint {

    private final ViewState viewState;
    private final ViewSerializer viewSerializer;

    public ServewrightViewEndpoint(ViewState viewState, ViewSerializer viewSerializer) {
        this.viewState = viewState;
        this.viewSerializer = viewSerializer;
    }

    @GetMapping(value = "/{screen}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getView(
            @PathVariable("screen") String screen,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {
        View view = viewState.getOrLoad(screen);
        String etag = etagFor(view);
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(304)
                    .header(HttpHeaders.ETAG, etag)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.ETAG, etag)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(viewSerializer.serialize(view));
    }

    static String etagFor(View view) {
        return '"' + view.screen() + ':' + view.schemaVersion() + ':' + view.stateVersion() + '"';
    }
}
