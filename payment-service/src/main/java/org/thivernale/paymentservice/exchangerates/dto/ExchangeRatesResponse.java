package org.thivernale.paymentservice.exchangerates.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRatesResponse(String base, Map<String, BigDecimal> rates) {
}
