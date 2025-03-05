package org.thivernale.orderservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "${spring.application.name} OpenAPI definition",
        version = "1.0"
    )
)
public class OpenApiConfig {
}
