package iam.bestbooks.controller;

import iam.bestbooks.model.Book;
import iam.bestbooks.repository.BookRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public record BookController (BookRepository bookRepository){

//    @SchemaMapping(typeName = "Query", value = "allBooks")
    @QueryMapping
    public List<Book> allBooks(){
        return bookRepository.findAll();
    }

    @QueryMapping
    public Book findBook(@Argument Integer id){
        return bookRepository.findOne(id);
    }
}
