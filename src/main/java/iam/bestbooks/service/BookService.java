package iam.bestbooks.service;

import iam.bestbooks.dto.AuthorInput;
import iam.bestbooks.dto.BookInput;
import iam.bestbooks.dto.RatingInput;
import iam.bestbooks.entity.Author;
import iam.bestbooks.entity.Book;
import iam.bestbooks.entity.Rating;
import iam.bestbooks.repository.AuthorRepository;
import iam.bestbooks.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public record BookService(BookRepository bookRepository,
                          AuthorRepository authorRepository) {

    public Page<Book> getAllPaginatedBooks(int pageNo, int pageSize){
        Pageable paging = PageRequest.of(pageNo, pageSize);
        return bookRepository.findAll(paging);
    }

    public Book getOneBook(Integer bookId){
        return bookRepository.findById(bookId)
                .orElseThrow(
                        () -> new RuntimeException("Book not found")
                );
    }

    public Book createBook(BookInput bookInput){
        if (bookInput == null) {
            throw new IllegalArgumentException("BookInput is null");
        }
        AuthorInput authorInput = bookInput.authorInput();
        RatingInput ratingInput = bookInput.ratingInput();

        Author author = new Author(null, authorInput.firstName(), authorInput.lastName());
        Rating rating = Rating.valueOf(ratingInput.name());
        // Save author first
        authorRepository.save(author);
        Book tobeCreated = new Book(
                null,
                bookInput.title(),
                rating,
                author
        );
        return bookRepository.save(tobeCreated);
    }
}
