package iam.bestbooks.model;

public record Book (
        Integer id,
        String title,
        Rating rating,
        Author author
) {
}
