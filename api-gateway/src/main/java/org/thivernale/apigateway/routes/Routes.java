package org.thivernale.apigateway.routes;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Configuration
@RequiredArgsConstructor
public class Routes {

    private final SwaggerUiConfigProperties swaggerUiConfigProperties;

    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        RouteLocatorBuilder.Builder routes = builder.routes();

        swaggerUiConfigProperties.getUrls()
            .forEach(url -> routes
                .route(url.getName() + "_swagger", r -> r.path(url.getUrl())
                    .filters(
                        f -> f.setPath("/v3/api-docs")
                            .circuitBreaker(c -> c.setName(url.getName() + "circuitBreaker")
                                .setFallbackUri("/fallback"))
                    )
                    .uri("lb://" + url.getUrl()
                        .replaceAll("/aggregate/([\\w-]+)/v3/api-docs", "$1"))));

        return routes
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return route(path("/fallback"), request -> status(SERVICE_UNAVAILABLE)
            .bodyValue("Service Unavailable for %s %s".formatted(
                request.method(),
                request.exchange()
                    .getAttribute("org.springframework.cloud.gateway.support.ServerWebExchangeUtils" +
                        ".gatewayOriginalRequestUrl")
            )));
    }
}
