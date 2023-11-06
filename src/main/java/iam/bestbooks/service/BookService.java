package iam.bestbooks.service;

import iam.bestbooks.entity.Book;
import iam.bestbooks.repository.AuthorRepository;
import iam.bestbooks.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public record BookService(BookRepository bookRepository,
                          AuthorRepository authorRepository) {

    public List<Book> getAllBooks(){
       return bookRepository.findAll();
    }

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
}
