package org.thivernale.paymentservice.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.dto.CreatePaymentTransactionRequest;
import org.thivernale.paymentservice.dto.CreatePaymentTransactionResponse;
import org.thivernale.paymentservice.model.BankAccount;
import org.thivernale.paymentservice.model.Payment;
import org.thivernale.paymentservice.model.PaymentTransactionCommand;
import org.thivernale.paymentservice.notification.PaymentTransactionProducer;
import org.thivernale.paymentservice.service.BankAccountService;
import org.thivernale.paymentservice.service.PaymentService;
import org.thivernale.paymentservice.service.PaymentTransactionValidator;
import org.thivernale.paymentservice.util.JsonConverter;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionValidator validator;
    private final PaymentService paymentService;
    private final BankAccountService bankAccountService;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final JsonConverter jsonConverter;

    @Override
    @Transactional
    public void process(String requestId, String message) {
        CreatePaymentTransactionRequest request = jsonConverter.toObject(message, CreatePaymentTransactionRequest.class);

        validator.validate(request);

        // TODO currency conversion
        bankAccountService.findById(request.sourceBankAccountId())
            .ifPresent(bankAccount -> subtractFromBankAccountBalance(bankAccount, request.amount()));
        if (request.destBankAccountId() != null) {
            bankAccountService.findById(request.destBankAccountId())
                .ifPresent(bankAccount -> subtractFromBankAccountBalance(bankAccount, request.amount()
                    .negate()));
        }

        Payment payment = paymentService.save(request);

        CreatePaymentTransactionResponse response = new CreatePaymentTransactionResponse(
            payment.getId(),
            payment.getStatus(),
            null,
            payment.getCreatedAt()
        );

        // TODO send response on error - maybe in global error handler
        paymentTransactionProducer.sendCommandResult(
            paymentTransactionProducer.getResultTopic(),
            requestId,
            jsonConverter.toString(response),
            PaymentTransactionCommand.CREATE
        );
    }

    private void subtractFromBankAccountBalance(BankAccount bankAccount, BigDecimal delta) {
        bankAccount.setBalance(bankAccount.getBalance()
            .subtract(delta));
    }
}
