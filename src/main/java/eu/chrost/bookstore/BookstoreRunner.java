package eu.chrost.bookstore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("bookstore.runner.enabled")
@RequiredArgsConstructor
@Slf4j
class BookstoreRunner implements CommandLineRunner {
    private final BookService bookService;

    @Override
    public void run(String... args) throws Exception {
        var books = bookService.getAllBooks();
        log.info("All books {}", books);
    }
}
