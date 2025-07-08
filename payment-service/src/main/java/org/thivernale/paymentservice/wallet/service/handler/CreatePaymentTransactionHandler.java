package org.thivernale.paymentservice.wallet.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.util.JsonConverter;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.dto.CreatePaymentTransactionResponse;
import org.thivernale.paymentservice.wallet.model.CurrencyAccount;
import org.thivernale.paymentservice.wallet.model.PaymentTransaction;
import org.thivernale.paymentservice.wallet.model.PaymentTransactionCommand;
import org.thivernale.paymentservice.wallet.notification.PaymentTransactionProducer;
import org.thivernale.paymentservice.wallet.service.CurrencyAccountService;
import org.thivernale.paymentservice.wallet.service.PaymentTransactionService;
import org.thivernale.paymentservice.wallet.service.PaymentTransactionValidator;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionValidator validator;
    private final PaymentTransactionService paymentTransactionService;
    private final CurrencyAccountService currencyAccountService;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final JsonConverter jsonConverter;

    @Transactional
    @Override
    public void process(String requestId, String message) {
        CreatePaymentTransactionRequest request = jsonConverter.toObject(message, CreatePaymentTransactionRequest.class);

        validator.validate(request);

        PaymentTransaction paymentTransaction = saveOperation(request);

        CreatePaymentTransactionResponse response = new CreatePaymentTransactionResponse(
            paymentTransaction.getId(),
            paymentTransaction.getStatus(),
            null,
            paymentTransaction.getCreatedAt()
        );

        // TODO send response on error - maybe in global error handler
        paymentTransactionProducer.sendCommandResult(
            paymentTransactionProducer.getResultTopic(),
            requestId,
            jsonConverter.toString(response),
            PaymentTransactionCommand.CREATE
        );
    }

    PaymentTransaction saveOperation(CreatePaymentTransactionRequest request) {
        // TODO currency conversion - amount should always be in account currency
        currencyAccountService.findById(request.sourceCurrencyAccountId())
            .ifPresent(account -> subtractFromCurrencyAccountBalance(account, request.amount()));
        if (request.destCurrencyAccountId() != null) {
            currencyAccountService.findById(request.destCurrencyAccountId())
                .ifPresent(account -> subtractFromCurrencyAccountBalance(account, request.amount()
                    .negate()));
        }

        return paymentTransactionService.save(request);
    }

    private void subtractFromCurrencyAccountBalance(CurrencyAccount account, BigDecimal delta) {
        account.setBalance(account.getBalance()
            .subtract(delta));
        currencyAccountService.save(account);
    }
}
