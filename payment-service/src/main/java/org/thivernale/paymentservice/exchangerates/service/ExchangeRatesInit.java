package org.thivernale.paymentservice.exchangerates.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    name = "exchangerates.api.url"
)
@RequiredArgsConstructor
@Slf4j
public class ExchangeRatesInit implements CommandLineRunner {
    private final ExchangeRatesService exchangeRatesService;

    @Override
    public void run(String... args) {
        if (!exchangeRatesService.hasInitializedExchangeRates()) {
            var fetchedRates = exchangeRatesService.fetchRates();
            exchangeRatesService.updateRates(fetchedRates);
        } else {
            log.info("Exchange rates already available");
        }
    }
}
