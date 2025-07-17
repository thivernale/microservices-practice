package org.thivernale.stack;

import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

public class LocalStack extends Stack {
    private final Vpc vpc;

    public LocalStack(
        @Nullable final Construct scope,
        @Nullable final String id,
        @Nullable final StackProps props
    ) {
        super(scope, id, props);

        vpc = createVpc();
    }

    public static void main(String[] args) {
        // CDK app instance
        App app = new App(AppProps.builder()
            .outdir("./cdk.out")
            .build());
        // for conversion of java code into CloudFormation template
        StackProps props = StackProps.builder()
            .synthesizer(new BootstraplessSynthesizer())
            .build();

        new LocalStack(app, "localstack", props);
        app.synth();

        System.out.println("App synthesizing in progress...");
    }

    private Vpc createVpc() {
        return Vpc.Builder
            .create(this, "MicroservicesPracticeVPC")
            .vpcName("MicroservicesPracticeVPC")
            .maxAzs(2)
            .build();
    }
}
