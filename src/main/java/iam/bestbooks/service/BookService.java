package iam.bestbooks.service;

import iam.bestbooks.dto.BookInput;
import iam.bestbooks.dto.RatingInput;
import iam.bestbooks.entity.Book;
import iam.bestbooks.enums.Rating;
import iam.bestbooks.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public record BookService(BookRepository bookRepository,
                          AuthorService authorService) {
    private static final String BOOK_NOT_FOUND = "Book not found";

    public Page<Book> getAllPaginatedBooks(int pageNo, int pageSize){
        Pageable paging = PageRequest.of(pageNo, pageSize);
        return bookRepository.findAll(paging);
    }

    public Book getOneBook(Integer bookId){
        return bookRepository.findById(bookId)
                .orElseThrow(
                        () -> new RuntimeException(BOOK_NOT_FOUND)
                );
    }

    public Book createBook(BookInput bookInput){
        // Get ratings from bookInput
        RatingInput ratingInput = bookInput.ratingInput();
        Rating rating = Rating.valueOf(ratingInput.name());
        // Save author first
        var savedAuthor = authorService.addAuthor(bookInput.authorInput());

        Book tobeCreated = new Book(
                null,
                bookInput.title(),
                rating,
                bookInput.price(),
                LocalDateTime.now(),
                savedAuthor
        );
        return bookRepository.save(tobeCreated);
    }

    public Book updateBook(Integer bookId, BookInput bookInput){
        var bookToUpdate = this.getOneBook(bookId);
        // Get ratings from bookInput
        RatingInput ratingInput = bookInput.ratingInput();
        Rating rating = Rating.valueOf(ratingInput.name());
        // Update the author object before the book object
        var updatedAuthor = authorService.updateAuthor(bookToUpdate.getAuthor().getId(), bookInput.authorInput());
        bookToUpdate.setTitle(bookInput.title());
        bookToUpdate.setRating(rating);
        bookToUpdate.setPrice(bookInput.price());
        bookToUpdate.setAuthor(updatedAuthor);
        // Save the object
        return bookRepository.save(bookToUpdate);
    }

    public Book deleteBook( Integer bookId){
        var bookToDelete = bookRepository.findById(bookId)
                .orElseThrow(
                        () -> new RuntimeException(BOOK_NOT_FOUND)
                );
        bookRepository.delete(bookToDelete);
        return bookToDelete;
    }
}
