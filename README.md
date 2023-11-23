# BestBooks
This is just a simple project to refresh my GraphQL knowledge

### General Info
This project uses GraphQL and a simple Book Registry with details like title, price, and author.

## Technologies
+ Spring Boot
+ GraphQL
+ GraphQLTest + JUnit + Mockito
+ H2 Database

## Usage:
#### Endpoint
```http
  http://localhost:8080/graphql
```
Some graphQL queries and mutations used:
### Get all books
```graphql
  #Query
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
```
Variables:
```json
{
"page":1,
"size": 10
}
```

### Create book
```graphql
  #Mutation
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
```
Variables:
```json
{
  "bookInput": {
    "title": "Pab's Delight",
    "price": 850.23,
    "ratingInput": "THREE_STAR",
    "authorInput": {"firstName": "Yaw", "lastName": "Smith"}
  }
}
```


## Future Work
 N/A