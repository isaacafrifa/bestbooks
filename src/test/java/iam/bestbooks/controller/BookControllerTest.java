package iam.bestbooks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import iam.bestbooks.config.GraphQLConfig;
import iam.bestbooks.entity.Author;
import iam.bestbooks.entity.Book;
import iam.bestbooks.enums.Rating;
import iam.bestbooks.repository.AuthorRepository;
import iam.bestbooks.repository.BookRepository;
import iam.bestbooks.service.AuthorService;
import iam.bestbooks.service.BookService;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    @Autowired
    AuthorService authorService;
    private List<Book> books = new ArrayList<>();

    @BeforeEach
    void setUp() {
        if (books.isEmpty()) {
            loadBooks();
        }
    }

    @Test
    void itShouldReturnAllBooks() {
        // Given
        int pageNo = 0;
        int pageSize = 10;

        //language=GraphQl
        String query = """
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

        // When + Then
        graphQlTester.document(query)
                .variable("page", pageNo)
                .variable("size", pageSize)
                .execute()
                .path("getAllBooks")
                .entityList(Book.class)
                .hasSize(3);
    }


    @Test
    void itShouldReturnOneBook() {
        // Given
        Integer bookId = 1;

        //language=GraphQl
        String query = """
                       query getBook($id: ID){
                           getBook(id: $id){
                           id
                           title
                           price
                           createdOn
                           rating {
                              star
                           }
                           author {
                               id
                               firstName
                               lastName
                           }
                         }
                       }
                """;
        // Mock the behavior of bookRepository.findById
        when(bookRepository.findById(bookId)).thenReturn(Optional.ofNullable(books.get(0)));

        // When + Then
        graphQlTester.document(query)
                .variable("id", bookId)
                .execute()
                .path("getBook")
                .entity(Book.class)
                .satisfies(
                        book -> {
                            assertEquals("Arms and the Man", book.getTitle());
                            assertEquals(BigDecimal.valueOf(610.57), book.getPrice());
                            assertEquals("Lanora", book.getAuthor().getFirstName());
                        }
                );
    }

    @Test
    void itShouldThrowBookNotFoundException() {
        // Given
        Integer bookId = 10;

        //language=GraphQl
        String query = """
                       query getBook($id: ID){
                           getBook(id: $id){
                           id
                           title
                           price
                           createdOn
                           rating {
                              star
                           }
                           author {
                               id
                               firstName
                               lastName
                           }
                         }
                       }
                """;

        // Mock the behavior of bookRepository.findById to throw BookNotFoundException
        when(bookRepository.findById(bookId)).thenThrow(new RuntimeException("Book not found"));

        // When
        var response = graphQlTester.document(query)
                .variable("id", bookId)
                .execute();

        // Then
        var actual = response.errors();
        assertNotNull(actual.satisfy(
                errors -> {
                    assertEquals(1, errors.size());
                    assertNotNull(actual, "Book not found");
                }
        ));
    }

    @Test
    void itShouldCreateNewBook() throws JsonProcessingException {
        // Given
        String bookToInsertJson = """
                {
                  "bookInput": {
                      "title": "Pab's Delight",
                    	"price": 850.23,
                      "ratingInput": "THREE_STAR",
                      "authorInput": {"firstName": "Isaac", "lastName": "Afrifa"}
                    }
                }
                """;

        Author createdAuthor = new Author(1, "Isaac", "Afrifa");
        Book createdBook = new Book(1,
                "Pab's Delight",
                Rating.THREE_STAR,
                BigDecimal.valueOf(850.23),
                LocalDate.now(),
                createdAuthor
        );
        // Parse bookToInsertJson into a Map
        Map<String, Object> variables = objectMapper.readValue(bookToInsertJson, new TypeReference<Map<String, Object>>() {
        });
        // Mock the book repository to return the created book when save is called
        when(bookRepository.save(any())).thenReturn(createdBook);

        // language=GraphQL
        String mutation = """
                   mutation create($bookInput: BookInput) {
                     createBook(bookInput: $bookInput) {
                       id
                       title
                       price
                       createdOn
                       author {
                         id
                         firstName
                         lastName
                       }
                     }
                   }
                """;
        // When
        GraphQlTester.Entity<Book, ?> actual = graphQlTester.document(mutation)
                .variable("bookInput", variables.get("bookInput"))
                .execute()
                .path("createBook")
                .entity(Book.class);

        // Then
        actual.satisfies(newBook -> {
            assertNotNull(newBook.getId());
            assertEquals(createdBook.getTitle(), newBook.getTitle());
            assertNotNull(newBook.getCreatedOn());
            assertEquals(createdBook.getAuthor().getFirstName(), newBook.getAuthor().getFirstName());
            assertEquals(createdBook.getAuthor().getLastName(), newBook.getAuthor().getLastName());
        });
    }

    @Test
    void itShouldUpdateBook() throws JsonProcessingException {
        // Given
        String bookRequestJson = """
                {
                  "bookInput": {
                      "title": "Pab's Delight",
                    	"price": 850.23,
                      "ratingInput": "THREE_STAR",
                      "authorInput": {"firstName": "Isaac", "lastName": "Afrifa"}
                    }
                }
                """;

        // Parse bookToInsertJson into a Map
        Map<String, Object> variables = objectMapper.readValue(bookRequestJson, new TypeReference<Map<String, Object>>() {
        });

        Author savedAuthor = new Author(91, "Lanora", "Veum");
        Author requestAuthor = new Author(1, "Isaac", "Afrifa");
        Book requestBook = new Book(1,
                "Pab's Delight",
                Rating.THREE_STAR,
                BigDecimal.valueOf(850.23),
                LocalDate.now(),
                requestAuthor
        );
        // Mock the book repository to return an existing book when findById is called
        when(bookRepository.findById(any())).thenReturn(Optional.ofNullable(books.get(0)));
        when(authorRepository.findById(any())).thenReturn(Optional.of(savedAuthor));
        when(bookRepository.save(any())).thenReturn(requestBook);

        // language=GraphQL
        String mutation = """
                mutation update($bookId: ID, $bookInput: BookInput) {
                  updateBook(bookId: $bookId, bookInput: $bookInput) {
                    id
                    title
                    price
                    createdOn
                    author {
                      id
                      firstName
                      lastName
                    }
                  }
                }
                """;

        // When
        GraphQlTester.Entity<Book, ?> actual = graphQlTester.document(mutation)
                .variable("bookId", 1)
                .variable("bookInput", variables.get("bookInput"))
                .execute()
                .path("updateBook")
                .entity(Book.class);

        // Then
        actual.satisfies(updatedBook -> {
            assertNotNull(updatedBook.getId());
            assertEquals("Pab's Delight", updatedBook.getTitle());
            assertEquals(BigDecimal.valueOf(850.23), updatedBook.getPrice());
            assertEquals("Isaac", updatedBook.getAuthor().getFirstName());
        });
    }

    @Test
    void itShouldDeleteBook() {
        // Given
        Integer bookId = 1;
        Book bookToDelete = books.get(0);

        // Mock the book repository to return an existing book when findById is called
        when(bookRepository.findById(any())).thenReturn(Optional.ofNullable(bookToDelete));

        // Mock the book repository to return the deleted book when delete is called
        when(bookRepository.save(any())).thenReturn(bookToDelete);

        // language=GraphQL
        String mutation = """
            mutation delete($bookId: ID) {
              deleteBook(bookId: $bookId) {
                id
                title
                price
                createdOn
                author {
                  id
                  firstName
                  lastName
                }
              }
            }
            """;

        // When
        GraphQlTester.Entity<Book, ?> actual = graphQlTester.document(mutation)
                .variable("bookId", bookId)
                .execute()
                .path("deleteBook")
                .entity(Book.class);

        // Then
        actual.satisfies(deletedBook -> {
            assertNotNull(deletedBook.getId());
            assertNotNull(bookToDelete);
            assertEquals(bookToDelete.getTitle(), deletedBook.getTitle());
            assertEquals(bookToDelete.getPrice(), deletedBook.getPrice());
            assertNotNull(deletedBook.getCreatedOn());
            assertNotNull(deletedBook.getAuthor());
        });

        // Verify that the delete method was called with the correct book
        verify(bookRepository, times(1)).delete(eq(bookToDelete));
    }


    private void loadBooks() {
        TypeReference<List<Book>> typeReference = new TypeReference<>() {
        };
        InputStream inputStream = TypeReference.class.getResourceAsStream("/data/books.json");
        try {
            books = objectMapper.readValue(inputStream, typeReference);
            System.out.println("Books loaded!");
        } catch (IOException e) {
            System.out.println("Unable to save: " + e.getMessage());
        }
    }

}