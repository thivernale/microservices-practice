package org.thivernale.paymentservice.exchangerates.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.thivernale.paymentservice.exchangerates.dto.ExchangeRatesResponse;

public interface ExchangeRateClientService {
    @GetExchange(accept = MediaType.APPLICATION_JSON_VALUE)
    ExchangeRatesResponse getExchangeRates(
        @RequestParam(name = "base", defaultValue = "USD", required = false) String base,
        @RequestParam(name = "symbols", defaultValue = "", required = false) String symbols
    );
}
