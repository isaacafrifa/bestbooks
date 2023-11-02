package iam.bestbooks.repository;

import iam.bestbooks.model.Book;
import iam.bestbooks.model.Rating;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepository {
    private final AuthorRepository authorRepository;
    private final List<Book> books = new ArrayList<>();

    public List<Book> findAll() {
        return books;
    }

    public Book findOne(Integer id) {
        return books.stream()
                .filter(book -> book.id().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Book not found")
                );
    }

    @PostConstruct
    private void init() {
        books.add(new Book(1, "Adventures of Hercules", Rating.NO_STAR, authorRepository.findByName("Jack Harrison")));
        books.add(new Book(2, "Exploring Testcontainers", Rating.THREE_STAR, authorRepository.findByName("Tyrick Mitchell")));
        books.add(new Book(3, "Yaw Pabs", Rating.TWO_STAR, authorRepository.findByName("Pep Tuchel")));
    }
}
