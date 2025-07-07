package org.thivernale.paymentservice.wallet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.wallet.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.wallet.model.Refund;
import org.thivernale.paymentservice.wallet.repository.RefundRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundService {
    private final RefundRepository refundRepository;
    private final RefundMapper refundMapper;

    public Refund save(CancelPaymentTransactionRequest request) {
        return refundRepository.save(refundMapper.toRefund(request));
    }
}
