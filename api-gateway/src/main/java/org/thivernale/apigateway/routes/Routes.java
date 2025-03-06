package org.thivernale.apigateway.routes;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                    .filters(f -> f.setPath("/v3/api-docs"))
                    .uri("lb://" + url.getUrl()
                        .replaceAll("/aggregate/([\\w-]+)/v3/api-docs", "$1"))));

        return routes
            .build();
    }
}
