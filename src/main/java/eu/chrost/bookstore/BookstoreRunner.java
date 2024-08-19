package eu.chrost.bookstore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@ConditionalOnProperty("bookstore.runner.enabled")
@RequiredArgsConstructor
@Slf4j
class BookstoreRunner implements CommandLineRunner {
    private final BookService bookService;
    private final PlatformTransactionManager transactionManager;
    private final AuditService auditService;

    @Override
    public void run(String... args) throws Exception {
        new TransactionTemplate(transactionManager).executeWithoutResult(transactionStatus -> {
            bookService.addBook("Nie-Boska Komedia", 150, "Zygmunt", "Krasiński");
            bookService.addBook("Konrad Wallenrod", 150, "Adam", "Mickiewicz");

            log.info("Adam Mickiewicz books: {}", bookService.getBooksOfGivenAuthor("Adam", "Mickiewicz"));
            log.info("Zygmunt Krasiński books: {}", bookService.getBooksOfGivenAuthor("Zygmunt", "Krasiński"));

            transactionStatus.setRollbackOnly();
        });
        log.info("Audit messages: {}", auditService.getAuditMessages());
    }
}
