package eu.chrost.bookstore;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Slf4j
class AuditServiceTest {

    @Container
    private static GenericContainer auditServer = new GenericContainer(DockerImageName.parse("chrosciu/simple-audit-server:1"))
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void registerServerProperties(DynamicPropertyRegistry registry) {
        registry.add("audit.server.url",
                () -> String.format("http://%s:%d", auditServer.getHost(), auditServer.getFirstMappedPort()));
    }

    @Autowired
    private AuditService auditService;

    @Test
    void audit_entry_from_message_should_be_created_and_message_should_be_retrieved() {
        var auditEntryCreationResult = auditService.createAuditEntry("Something happened");
        assertThat(auditEntryCreationResult).isTrue();

        var auditMessages = auditService.getAuditMessages();
        assertThat(auditMessages).containsExactly("Something happened");
    }

}
