package iam.bestbooks.controller;

import iam.bestbooks.entity.Book;
import iam.bestbooks.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public record BookController (BookService bookService){

//    @SchemaMapping(typeName = "Query", value = "allBooks")
    @QueryMapping
    public List<Book> allBooks(){
        return bookService.getAllBooks();
    }

    @QueryMapping
    public Page<Book> allPaginatedBooks(@Argument int page, @Argument int size) {
        return bookService
                .getAllPaginatedBooks(page, size);
    }

    @QueryMapping
    public Book findBook(@Argument Integer id){
        return bookService.getOneBook(id);
    }
}
