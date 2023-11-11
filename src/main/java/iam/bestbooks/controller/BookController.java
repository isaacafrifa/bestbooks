package iam.bestbooks.controller;

import iam.bestbooks.dto.BookInput;
import iam.bestbooks.entity.Book;
import iam.bestbooks.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


@Controller
public record BookController (BookService bookService){

    @QueryMapping
    public Page<Book> getAllBooks(@Argument int page, @Argument int size) {
        return bookService
                .getAllPaginatedBooks(page, size);
    }

    @QueryMapping
    public Book getBook(@Argument Integer id){
        return bookService.getOneBook(id);
    }

    @MutationMapping
    public Book createBook(@Argument BookInput bookInput){
        return bookService.createBook(bookInput);
    }


}
