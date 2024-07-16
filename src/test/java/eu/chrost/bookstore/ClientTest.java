package eu.chrost.bookstore;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Slf4j
class ClientTest {

    @Container
    private static GenericContainer httpServer = new GenericContainer(DockerImageName.parse("strm/helloworld-http"))
            .withExposedPorts(80);

    @DynamicPropertySource
    static void registerServerProperties(DynamicPropertyRegistry registry) {
        registry.add("server.url",
                () -> String.format("http://%s:%d", httpServer.getHost(), httpServer.getFirstMappedPort()));
    }

    @Autowired
    private ClientService clientService;

    @Test
    void server_should_respond_with_hello() {
        assertThat(clientService.getHello()).contains("HTTP Hello World");
    }
}
