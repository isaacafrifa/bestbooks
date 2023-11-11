package iam.bestbooks.dto;

public record BookInput(
        String title,
        RatingInput ratingInput,
        AuthorInput authorInput) {
}

