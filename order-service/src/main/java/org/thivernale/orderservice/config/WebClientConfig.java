package org.thivernale.orderservice.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.thivernale.orderservice.client.InventoryService;

import java.time.Duration;

@Configuration
public class WebClientConfig {
    @Value("${app.urls.inventory-service:http://inventory-service}")
    private String inventoryServiceUrl;

    /**
     * See {@link RestClientAutoConfiguration} autoconfigured bean
     */
    @Bean
    //@LoadBalanced
    public RestClient.Builder restClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer) {
        return restClientBuilderConfigurer.configure(RestClient.builder()
            .requestFactory(clientHttpRequestFactory()));
    }

    @Bean
    ClientHttpRequestFactory clientHttpRequestFactory() {
        return ClientHttpRequestFactoryBuilder.detect()
            .build(ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(Duration.ofSeconds(5))
                .withReadTimeout(Duration.ofSeconds(5)));
    }

    @Bean
    public InventoryService inventoryService(
        RestClient.Builder restClientBuilder,
        ObservationRegistry observationRegistry
    ) {
        RestClient restClient = restClientBuilder.baseUrl(inventoryServiceUrl)
            .requestFactory(clientHttpRequestFactory())
            .observationRegistry(observationRegistry)
            .build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter)
            .build();
        return httpServiceProxyFactory.createClient(InventoryService.class);
    }
}
