package eu.chrost.bookstore;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("bookstore.audit.enabled")
@RequiredArgsConstructor
class BookstoreAuditEventListener {
    private final AuditService auditService;

    @EventListener
    private void onBookAdded(BookAddedEvent event) {
        auditService.createAuditEntry("A book has been added: " + event.bookWithAuthor());
    }
}
