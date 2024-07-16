package eu.chrost.bookstore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
class ClientService {

    @Value("${server.url:}")
    private String serverUrl;

    public String getHello() {
        return RestClient.create().get()
                .uri(serverUrl)
                .retrieve()
                .body(String.class);
    }
}
