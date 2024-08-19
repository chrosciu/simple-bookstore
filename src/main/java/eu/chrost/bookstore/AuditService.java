package eu.chrost.bookstore;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Component
class AuditService {

    @Data
    @Builder
    public static class AuditInputDto {
        private String message;
    }

    @Data
    public static class AuditOutputDto {
        private String message;
        private LocalDateTime timestamp;
    }

    @Value("${audit.server.url:}")
    private String serverUrl;

    public boolean createAuditEntry(String message) {
        var responseEntity = RestClient.create().post()
                .uri(serverUrl + "/audit")
                .body(AuditInputDto.builder().message(message).build())
                .retrieve()
                .toBodilessEntity();
        return responseEntity.getStatusCode().equals(HttpStatus.CREATED);
    }

    public List<String> getAuditMessages() {
        List<AuditOutputDto> auditEntries = RestClient.create().get()
                .uri(serverUrl + "/audit")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        return auditEntries.stream()
                .map(AuditOutputDto::getMessage)
                .toList();
    }
}
