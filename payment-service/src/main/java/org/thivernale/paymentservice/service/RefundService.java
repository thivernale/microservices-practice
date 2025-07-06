package org.thivernale.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thivernale.paymentservice.dto.CancelPaymentTransactionRequest;
import org.thivernale.paymentservice.model.Refund;
import org.thivernale.paymentservice.repository.RefundRepository;

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
