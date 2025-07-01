package org.thivernale.customerservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingServiceGrpcClient {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingResponse createBillingAccount(String customerId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder()
            .setClientId(customerId)
            .setName(name)
            .setEmail(email)
            .build();
        BillingResponse response = blockingStub.createBillingAccount(request);

        log.info("Received response from Billing Service via GRPC: {}", response);

        return response;
    }
}
