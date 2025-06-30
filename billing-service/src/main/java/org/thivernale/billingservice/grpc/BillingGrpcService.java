package org.thivernale.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest request, StreamObserver<BillingResponse> responseObserver) {
        log.info("createBillingAccount request received: {}", request);
//        super.createBillingAccount(request, responseObserver);

        BillingResponse billingResponse = BillingResponse.newBuilder()
            .setAccountId("account-id")
            .setStatus("success")
            .build();
        /*try {
            byte[] byteArray = billingResponse.toByteArray();
            BillingResponse parsed = BillingResponse.parseFrom(byteArray);
            log.info("original {} {}, parsed {} {}",
                System.identityHashCode(billingResponse), billingResponse.hashCode(),
                System.identityHashCode(parsed), parsed.hashCode());

        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }*/

        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted();
    }
}
