package org.thivernale.paymentservice.exchangerates.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thivernale.paymentservice.wallet.repository.CurrencyAccountRepository;

import java.math.BigDecimal;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = """
        exchangerates.api.key=
        exchangerates.api.url=https://openexchangerates.org/api/latest.json?app_id={app_id}&prettyprint=true&show_alternative=false
        spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
        """)
class ExchangeRatesServiceTest {
    @Autowired
    private ExchangeRatesService exchangeRatesService;
    @Autowired
    private CurrencyAccountRepository currencyAccountRepository;

    @Test
    public void testGetExchangeRates() {
        exchangeRatesService.fetchRates();
        BigDecimal balance = currencyAccountRepository.findById(1L)
            .orElseThrow(RuntimeException::new)
            .getBalance();
        System.out.println(balance.precision());
        System.out.println(balance);
    }
}
