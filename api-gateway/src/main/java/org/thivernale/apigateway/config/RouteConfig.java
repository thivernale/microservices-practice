package org.thivernale.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app")
@EnableConfigurationProperties
@Getter
@Setter
public class RouteConfig {
    private final Map<String, String> urls = new HashMap<>();
}
