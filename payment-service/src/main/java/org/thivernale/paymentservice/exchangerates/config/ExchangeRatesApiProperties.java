package org.thivernale.paymentservice.exchangerates.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "exchangerates.api")
@Data
public class ExchangeRatesApiProperties {
    private final String key;
    private final String url;
}
