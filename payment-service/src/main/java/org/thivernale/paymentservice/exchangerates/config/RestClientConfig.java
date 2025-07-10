package org.thivernale.paymentservice.exchangerates.config;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;
import org.thivernale.paymentservice.exchangerates.client.ExchangeRateClientService;

import java.time.Duration;

@EnableConfigurationProperties({ExchangeRatesApiProperties.class})
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final ExchangeRatesApiProperties exchangeRatesApiProperties;

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
    public ExchangeRateClientService exchangeRateClientService(
        RestClient.Builder restClientBuilder,
        ObservationRegistry observationRegistry
    ) {
        UriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();

        RestClient restClient = restClientBuilder.baseUrl(
                uriBuilderFactory.expand(exchangeRatesApiProperties.getUrl(), exchangeRatesApiProperties.getKey())
            )
            .requestFactory(clientHttpRequestFactory())
            .observationRegistry(observationRegistry)
            .build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter)
            .build();

        return httpServiceProxyFactory.createClient(ExchangeRateClientService.class);
    }
}
