package iam.bestbooks.util;

import com.github.javafaker.Faker;
import iam.bestbooks.entity.Author;
import iam.bestbooks.entity.Book;
import iam.bestbooks.enums.Rating;
import iam.bestbooks.repository.AuthorRepository;
import iam.bestbooks.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final BookRepository repository;
    private final AuthorRepository authorRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) {
        Faker faker = new Faker();
        // Add 100 authors
        List<Author> authors = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new Author(
                        null,
                        faker.name().firstName(),
                        faker.name().lastName()
                )).toList();

        // Save the authors first
        authorRepository.saveAll(authors);

        // add 100 books
        List<Book> books = IntStream.rangeClosed(1,100)
                .mapToObj(i -> new Book(
                        null,
                        faker.book().title(),
                        generateRandomRating(),
                        authors.get(i - 1) // Get the corresponding author from the list
                )).toList();

        repository.saveAll(books);
    }

    private Rating generateRandomRating() {
        Rating[] ratings = Rating.values();
        return ratings[random.nextInt(ratings.length)];
    }
}
