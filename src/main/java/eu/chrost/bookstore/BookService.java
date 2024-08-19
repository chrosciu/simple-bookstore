package eu.chrost.bookstore;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class BookService {
    private final JdbcClient jdbcClient;
    private final AuditService auditService;

    public List<Book> getAllBooks() {
        return jdbcClient.sql("""
                        select * from books
                        """)
                .query(Book.class)
                .list();
    }

    public List<BookWithAuthor> getBooksOfGivenAuthor(String authorFirstName, String authorLastName) {
        return jdbcClient.sql("""
                        select b.title, a.first_name, a.last_name
                        from books b
                            join authors a on a.id = b.author_id
                        where a.first_name = :authorFirstName
                            and a.last_name = :authorLastName
                        """)
                .param("authorFirstName", authorFirstName)
                .param("authorLastName", authorLastName)
                .query(BookWithAuthor.class)
                .list();
    }

    private Optional<Long> getAuthorId(String authorFirstName, String authorLastName) {
        return jdbcClient.sql("""
                        select id
                        from authors
                        where first_name = :authorFirstName
                        and last_name = :authorLastName
                        """)
                .param("authorFirstName", authorFirstName)
                .param("authorLastName", authorLastName)
                .query(Long.class)
                .optional();
    }

    private Long addAuthor(String authorFirstName, String authorLastName) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                        insert into authors(id, first_name, last_name)
                        values(nextval('authors_seq'), :authorFirstName, :authorLastName)
                        returning id;
                        """)
                .param("authorFirstName", authorFirstName)
                .param("authorLastName", authorLastName)
                .update(keyHolder);
        return keyHolder.getKeyAs(Long.class);
    }

    @Transactional
    public Long addBook(String title, int pages, String authorFirstName, String authorLastName) {
        var authorId = getAuthorId(authorFirstName, authorLastName)
                .orElseGet(() -> addAuthor(authorFirstName, authorLastName));
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("""
                        insert into books(id, author_id, title, pages)
                        values(nextval('books_seq'), :authorId, :title, :pages)
                        returning id;
                        """)
                .param("authorId", authorId)
                .param("title", title)
                .param("pages", pages)
                .update(keyHolder);
        var bookId = keyHolder.getKeyAs(Long.class);
        var bookWithAuthor = new BookWithAuthor(authorFirstName, authorLastName, title);
        auditService.createAuditEntry("A book has been added: " + bookWithAuthor);
        return bookId;
    }

    public boolean authorExists(String authorFirstName, String authorLastName) {
        return jdbcClient.sql("""
                        select count(*)
                        from authors
                        where first_name = :authorFirstName 
                            and last_name = :authorLastName
                        """)
                .param("authorFirstName", authorFirstName)
                .param("authorLastName", authorLastName)
                .query(Long.class)
                .single() > 0;
    }
}
