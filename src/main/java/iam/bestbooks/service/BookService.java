package iam.bestbooks.service;

import iam.bestbooks.entity.Book;
import iam.bestbooks.repository.AuthorRepository;
import iam.bestbooks.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record BookService(BookRepository bookRepository,
                          AuthorRepository authorRepository) {

    public List<Book> getAllBooks(){
       return bookRepository.findAll();
    }

    public Book getOneBook(Integer bookId){
        return bookRepository.findById(bookId)
                .orElseThrow(
                        () -> new RuntimeException("Book not found")
                );
    }
}
