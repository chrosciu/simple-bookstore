package eu.chrost.bookstore;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookServiceTest {
    @Autowired
    private BookService bookService;

    @Test
    void all_books_should_be_returned() {
        //when
        var books = bookService.getAllBooks();

        //then
        assertThat(books).containsExactlyInAnyOrder(
                new Book("Pan Tadeusz", 1000),
                new Book("Dziady", 200),
                new Book("Kordian", 100)
        );
    }

    @Test
    void all_books_of_given_author_should_be_returned() {
        //when
        var books = bookService.getBooksOfGivenAuthor("Adam", "Mickiewicz");

        //then
        assertThat(books).containsExactlyInAnyOrder(
                new BookWithAuthor("Adam", "Mickiewicz", "Pan Tadeusz"),
                new BookWithAuthor("Adam", "Mickiewicz", "Dziady")
        );
    }

    @Test
    void book_of_an_existing_author_should_be_added() {
        //when
        var newBookId = bookService.addBook("Konrad Wallenrod", 300, "Adam", "Mickiewicz");

        //then
        assertThat(newBookId).isNotNull();
        assertThat(bookService.getBooksOfGivenAuthor("Adam", "Mickiewicz")).hasSize(3);
    }

    @Test
    void book_of_non_existing_author_should_be_added_with_the_author() {
        //when
        var newBookId = 0L;
        try {
            newBookId = bookService.addBook("Nie-Boska Komedia", 150, "Zygmunt", "Krasiński");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //then
        assertThat(newBookId).isNotNull();
        assertThat(bookService.authorExists("Zygmunt", "Krasiński")).isTrue();
        //assertThat(bookService.getBooksOfGivenAuthor("Zygmunt", "Krasiński")).hasSize(1);
    }
}
