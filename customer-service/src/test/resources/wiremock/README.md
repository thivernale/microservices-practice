Testcontainers + WireMock https://wiremock.org/docs/solutions/testcontainers/

WireMock + gRPC https://wiremock.org/docs/grpc/

WireMock with extensions in Docker container https://hub.docker.com/r/wiremock/wiremock#using-wiremock-extensions

Putting it all together:

Download extension jar file
from [maven repo](https://repo1.maven.org/maven2/org/wiremock/wiremock-grpc-extension-standalone/)
into [extensions](extensions) folder (file is big therefore not checked out in git).

Extension jar file is loaded into container
in [TestContainersConfiguration.java](../../java/org/thivernale/customerservice/TestcontainersConfiguration.java)

```
wiremockServer = new WireMockContainer("wiremock/wiremock:3.13.0")
    ...
    .withExtensions(
        List.of(),
        new DefaultResourceLoader(TestcontainersConfiguration.class.getClassLoader())
            .getResource("wiremock/extensions")
            .getFile()
);
```
