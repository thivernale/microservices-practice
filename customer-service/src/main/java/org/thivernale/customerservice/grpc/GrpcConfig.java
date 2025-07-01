package org.thivernale.customerservice.grpc;

import billing.BillingServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GrpcConfig {
    @Value("${billing.service.address:localhost}")
    private String billingServiceAddress;
    @Value("${billing.service.port:9099}")
    private int billingServicePort;

    @Bean
    BillingServiceGrpc.BillingServiceBlockingStub blockingStub() {
        log.info("Connecting to Billing Service GRPC at {}:{}", billingServiceAddress, billingServicePort);

        return BillingServiceGrpc.newBlockingStub(ManagedChannelBuilder.forAddress(billingServiceAddress,
                billingServicePort)
            .usePlaintext()
            .build());
    }
}
