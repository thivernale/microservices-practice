package org.thivernale.paymentservice.wallet.service;

import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.model.BankAccount;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.model.CurrencyType;

import java.math.BigDecimal;

public final class TestDataUtil {

    private TestDataUtil() {
    }

    public static CurrencyAccount getCurrencyAccount(Long id) {
        return CurrencyAccount.builder()
            .id(id)
            .balance(new BigDecimal("1000.00"))
            .currency(CurrencyType.BGN)
            .bankAccount(getBankAccount(id))
            .build();
    }

    public static BankAccount getBankAccount(Long id) {
        return BankAccount.builder()
            .id(id)
            .build();
    }

    public static CreatePaymentTransactionRequest createPaymentRequest() {
        return new CreatePaymentTransactionRequest(
            BigDecimal.valueOf(200),
            1L,
            2L,
            null
        );
    }

    public static CancelPaymentTransactionRequest createRefundRequest() {
        return new CancelPaymentTransactionRequest(
            1L,
            BigDecimal.valueOf(200),
            "refund"
        );
    }
}
