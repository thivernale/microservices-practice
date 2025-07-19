package org.thivernale.stack;

import org.apache.commons.text.CaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.amazon.awscdk.*;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.docdb.DatabaseCluster;
import software.amazon.awscdk.services.docdb.Login;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;
import software.constructs.Construct;

import java.util.*;

public class LocalStack extends Stack {
    private final Vpc vpc;

    private final Cluster escCluster;

    public LocalStack(
        @Nullable final Construct scope,
        @Nullable final String id,
        @Nullable final StackProps props
    ) {
        super(scope, id, props);

        vpc = createVpc();

        Map<String, DatabaseInstance> rdbs = new LinkedHashMap<>();
        Map<String, CfnHealthCheck> healthChecks = new LinkedHashMap<>();

        List.of("inventory-service-db", "order-service-db", "payment-service-db")
            .forEach(dbName -> {
                String idString = CaseUtils.toCamelCase(dbName, true, '-');
                DatabaseInstance databaseInstance = createMysqlDatabase(idString, dbName);
                rdbs.put(idString, databaseInstance);
                healthChecks.put(
                    idString + "HealthCheck",
                    createDbHealthCheck(databaseInstance, idString + "HealthCheck"));
            });

        DatabaseCluster databaseCluster = createDatabaseCluster();
        var customerServiceDb = createMongoDatabase("CustomerServiceDb", "customer-service-db", databaseCluster);

        // Kafka cluster
        CfnCluster mskCluster = createMskCluster();

        // ECS cluster
        escCluster = createEcsCluster();

        // create ECS services
        List<FargateServiceParams> serviceParams = List.of(
            new FargateServiceParams("api-gateway", List.of(8080), null, null),
            new FargateServiceParams("billing-service", List.of(8087, 9099), null, null),
            new FargateServiceParams("config-server", List.of(8888), null, null),
// TODO mongodb for service
//            new FargateServiceParams("customer-service", List.of(8090), null, Map.of(
//                    "BILLING_SERVICE_ADDRESS", "host.docker.internal",
//                    "BILLING_SERVICE_PORT", "9099",
//                    "SPRING_DATA_MONGODB_URI", ""
//                    )),
            new FargateServiceParams("inventory-service", List.of(8082), rdbs.get("inventory-service-db"), null),
            new FargateServiceParams("notification-service", List.of(8085), null, Map.of()),
            new FargateServiceParams("order-service", List.of(8083), rdbs.get("order-service-db"), Map.of())
//            new  FargateServiceParams("payment-service", List.of(8088), rdbs.get("payment-service-db"), Map.of()),
//            new  FargateServiceParams("product-service", List.of(8084), null, Map.of()),
        );
        Set<String> servicesUsingKafka = Set.of("customer-service", "notification-service", "order-service", "payment" +
            "-service");

        Map<String, FargateService> serviceMap = new LinkedHashMap<>();
        serviceParams.forEach(params -> {
            FargateService fargateService = createFargateService(params);
            serviceMap.put(params.imageName(), fargateService);

            if (params.db() != null) {
                fargateService.getNode()
                    .addDependency(
                        params.db(),
                        healthChecks.get(CaseUtils.toCamelCase(params.imageName(), true, '-') + "DbHealthCheck")
                    );
            }

            if (servicesUsingKafka.contains(params.imageName())) {
                fargateService.getNode()
                    .addDependency(mskCluster);
            }

            if (params.additionalEnvVars() != null) {
                if (params.additionalEnvVars()
                    .containsKey("BILLING_SERVICE_ADDRESS")) {
                    fargateService.getNode()
                        .addDependency(serviceMap.get("billing-service"));
                }
                if (params.additionalEnvVars()
                    .containsKey("SPRING_DATA_MONGODB_URI")) {
                    fargateService.getNode()
                        .addDependency(customerServiceDb);
                }
            }
        });
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

    private DatabaseInstance createMysqlDatabase(String id, String dbName) {
        return DatabaseInstance.Builder
            .create(this, id)
            .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                .version(MysqlEngineVersion.VER_8_4_5)
                .build()))
            .vpc(vpc)
            .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
            .allocatedStorage(20)
            .credentials(Credentials.fromGeneratedSecret("admin_user"))
            .databaseName(dbName)
            .removalPolicy(RemovalPolicy.DESTROY)
            .build();
    }

    private CfnHealthCheck createDbHealthCheck(DatabaseInstance db, String id) {
        return CfnHealthCheck.Builder
            .create(this, id)
            .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                .type("TCP")
                .port(Token.asNumber(db.getDbInstanceEndpointPort()))
                .ipAddress(db.getDbInstanceEndpointAddress())
                .requestInterval(30)
                .failureThreshold(3)
                .build())
            .build();
    }

    private CfnCluster createMskCluster() {
        return CfnCluster.Builder.create(this, "MskCluster")
            .clusterName("kafka-cluster")
            .kafkaVersion("2.8.0")
            .numberOfBrokerNodes(1)
            .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                .instanceType("kafka.m5.xlarge")
                .clientSubnets(vpc.getPrivateSubnets()
                    .stream()
                    .map(ISubnet::getSubnetId)
                    .toList()
                )
                .brokerAzDistribution("DEFAULT")
                .build())
            .build();
    }

    private Cluster createEcsCluster() {
        return Cluster.Builder.create(this, "MicroservicesPracticeCluster")
            .vpc(vpc)
            // for service discovery
            .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                .name("microservices-practice.local")
                .build())
            .build();
    }

    private FargateService createFargateService(FargateServiceParams params) {

        String id = CaseUtils.toCamelCase(params.imageName(), true, '-');

        // ECS task definition
        FargateTaskDefinition taskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
            .cpu(256)
            .memoryLimitMiB(512)
            .build();

        ContainerDefinitionOptions.Builder containerDefinitionBuilder = ContainerDefinitionOptions.builder()
            // Localstack will get the image from local repository
            .image(ContainerImage.fromRegistry(params.imageName()))
            .portMappings(params.ports()
                .stream()
                .map(port -> PortMapping.builder()
                    .containerPort(port)
                    .hostPort(port)
                    .protocol(Protocol.TCP)
                    .build())
                .toList())
            .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                    .logGroupName("/ecs/" + params.imageName())
                    .removalPolicy(RemovalPolicy.DESTROY)
                    .retention(RetentionDays.ONE_DAY)
                    .build())
                .streamPrefix(params.imageName())
                .build()));

        Map<String, String> envVars = new HashMap<>();

        envVars.put(
            "SPRING_KAFKA_BOOTSTRAP_SERVERS",
            "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");
        if (params.additionalEnvVars() != null) {
            envVars.putAll(params.additionalEnvVars());
        }

        if (params.db() != null) {
            envVars.put("SPRING_DATASOURCE_URL", ("jdbc:mysql://%s:%s/%s-db").formatted(
                params.db()
                    .getDbInstanceEndpointAddress(),
                params.db()
                    .getDbInstanceEndpointPort(),
                params.imageName()
            ));
            envVars.put("SPRING_DATASOURCE_USERNAME", "admin_user");
            envVars.put("SPRING_DATASOURCE_PASSWORD", params.db()
                .getSecret()
                .secretValueFromJson("password")
                .toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
        }

        containerDefinitionBuilder.environment(envVars);

        // add a new container to task definition
        taskDefinition.addContainer(params.imageName() + "Container", containerDefinitionBuilder.build());

        // crate service to run the task
        return FargateService.Builder.create(this, id)
            .cluster(escCluster)
            .taskDefinition(taskDefinition)
            .assignPublicIp(false)
            .serviceName(params.imageName())
            .minHealthyPercent(100)
            /*.capacityProviderStrategies(List.of(CapacityProviderStrategy.builder()
                .capacityProvider("FARGATE_SPOT")
                .weight(2)
                .build(), CapacityProviderStrategy.builder()
                .capacityProvider("FARGATE")
                .weight(1)
                .build()))*/
            .build();
    }

    private software.amazon.awscdk.services.docdb.DatabaseInstance createMongoDatabase(
        String id,
        String dbName,
        DatabaseCluster databaseCluster
    ) {
        return software.amazon.awscdk.services.docdb.DatabaseInstance.Builder
            .create(this, id)
            .cluster(databaseCluster)
            .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
            .dbInstanceName(dbName)
            .removalPolicy(RemovalPolicy.DESTROY)
            .build();
    }

    private @NotNull DatabaseCluster createDatabaseCluster() {
        return DatabaseCluster.Builder.create(this, "DocdbCluster")
            .vpc(vpc)
            .instanceRemovalPolicy(RemovalPolicy.DESTROY)
            .masterUser(Login.builder()
                .username("myuser")
                .build())
            .instanceType(InstanceType.of(InstanceClass.MEMORY5, InstanceSize.LARGE))
            .vpcSubnets(SubnetSelection.builder()
                .subnetType(SubnetType.PUBLIC)
                .build())
            .build();
    }
}

record FargateServiceParams(
    String imageName,
    List<Integer> ports,
    DatabaseInstance db,
    Map<String, String> additionalEnvVars) {
}
