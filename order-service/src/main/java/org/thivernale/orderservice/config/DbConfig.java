package org.thivernale.orderservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing
public class DbConfig {
}
