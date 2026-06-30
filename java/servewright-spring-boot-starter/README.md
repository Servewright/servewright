# servewright-spring-boot-starter

Spring Boot auto-configuration for Servewright: view/action/SSE endpoints, Jackson serializers, `@OnAction` scanning.

## Maven

```xml
<dependency>
  <groupId>io.servewright</groupId>
  <artifactId>servewright-spring-boot-starter</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

## Endpoints

- `GET /servewright/view/{screen}`
- `POST /servewright/action`
- `GET /servewright/stream/{screen}` (SSE)

## License

Apache-2.0
