package iam.bestbooks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iam.bestbooks.config.GraphQLConfig;
import iam.bestbooks.entity.Book;
import iam.bestbooks.repository.AuthorRepository;
import iam.bestbooks.repository.BookRepository;
import iam.bestbooks.service.AuthorService;
import iam.bestbooks.service.BookService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@Import({BookService.class, AuthorService.class, GraphQLConfig.class})
@GraphQlTest(BookController.class)
class BookControllerTest {

    @Autowired
    GraphQlTester graphQlTester;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private BookRepository bookRepository;
    @MockBean
    private AuthorRepository authorRepository;
    @Autowired
    BookService bookService;
    private List<Book> books = new ArrayList<>();

    @BeforeEach
    void setUp() {
        if(books.isEmpty()) {
            loadBooks();
        }
    }

    @Test
    void itShouldReturnAllBooks() {
        int pageNo=0;
        int pageSize=10;
        //language=GraphQl
        String actualDocument = """
                query allBooks($page:Int!, $size:Int!){
                    getAllBooks(page: $page,size: $size){
                     id
                     title
                     price
                     createdOn
                     rating{
                       star
                     }
                     author{
                       id
                       firstName
                       lastName
                     }
                   }
                 }
                """;

        // Mock the behavior of bookRepository.findAll
        Page<Book> bookPage = new PageImpl<>(books, PageRequest.of(pageNo, pageSize), books.size());
        when(bookRepository.findAll(PageRequest.of(pageNo, pageSize))).thenReturn(bookPage);

        graphQlTester.document(actualDocument)
                .variable("page", pageNo)
                .variable("size",pageSize)
                .execute()
                .path("getAllBooks")
                .entityList(Book.class)
                .hasSize(3);
    }



    private void loadBooks() {

        TypeReference<List<Book>> typeReference = new TypeReference<>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/data/books.json");
        try {
            books = objectMapper.readValue(inputStream,typeReference);
            System.out.println("Books loaded!");
        } catch (IOException e){
            System.out.println("Unable to save: " + e.getMessage());
        }
    }

}