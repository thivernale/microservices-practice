package org.thivernale.paymentservice.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.dto.CancelPaymentTransactionResponse;
import org.thivernale.paymentservice.model.BankAccount;
import org.thivernale.paymentservice.model.PaymentTransactionCommand;
import org.thivernale.paymentservice.model.Refund;
import org.thivernale.paymentservice.notification.PaymentTransactionProducer;
import org.thivernale.paymentservice.service.BankAccountService;
import org.thivernale.paymentservice.service.PaymentService;
import org.thivernale.paymentservice.service.PaymentTransactionValidator;
import org.thivernale.paymentservice.service.RefundService;
import org.thivernale.paymentservice.util.JsonConverter;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CancelPaymentTransactionHandler implements PaymentTransactionCommandHandler {
    private final PaymentTransactionValidator validator;
    private final RefundService refundService;
    private final BankAccountService bankAccountService;
    private final PaymentService paymentService;
    private final PaymentTransactionProducer paymentTransactionProducer;
    private final JsonConverter jsonConverter;

    @Override
    public void process(String requestId, String message) {
        CancelPaymentTransactionRequest request = jsonConverter.toObject(message, CancelPaymentTransactionRequest.class);

        validator.validate(request);

        Refund refund = saveOperation(request);

        CancelPaymentTransactionResponse response = new CancelPaymentTransactionResponse(
            refund.getId(),
            refund.getStatus(),
            null,
            refund.getCreatedAt()
        );

        // TODO send response on error - maybe in global error handler
        paymentTransactionProducer.sendCommandResult(
            paymentTransactionProducer.getResultTopic(),
            requestId,
            jsonConverter.toString(response),
            PaymentTransactionCommand.REFUND
        );
    }

    @Transactional
    Refund saveOperation(CancelPaymentTransactionRequest request) {
        paymentService.findById(request.paymentTransactionId())
            .ifPresent(paymentTransaction -> {
                bankAccountService.findById(paymentTransaction.getSourceBankAccount()
                        .getId())
                    .ifPresent(bankAccount -> subtractFromBankAccountBalance(bankAccount, request.amount()
                        .negate()));
                if (paymentTransaction.getDestBankAccount() != null) {
                    bankAccountService.findById(paymentTransaction.getDestBankAccount()
                            .getId())
                        .ifPresent(bankAccount -> subtractFromBankAccountBalance(bankAccount, request.amount()));
                }
            });

        return refundService.save(request);
    }

    private void subtractFromBankAccountBalance(BankAccount bankAccount, BigDecimal delta) {
        bankAccount.setBalance(bankAccount.getBalance()
            .subtract(delta));
        bankAccountService.save(bankAccount);
    }
}
