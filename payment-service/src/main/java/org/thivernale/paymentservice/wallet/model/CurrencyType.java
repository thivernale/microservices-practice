package org.thivernale.paymentservice.wallet.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum CurrencyType {
    BGN,
    EUR,
    USD;

    public static CurrencyType fromString(String value) {
        for (CurrencyType currencyType : CurrencyType.values()) {
            if (currencyType.name()
                .equalsIgnoreCase(value)) {
                return currencyType;
            }
        }
        throw new IllegalArgumentException("Invalid currency type: " + value);
    }
}
