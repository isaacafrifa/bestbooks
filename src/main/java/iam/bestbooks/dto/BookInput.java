package iam.bestbooks.dto;

import java.math.BigDecimal;

public record BookInput(
        String title,
        RatingInput ratingInput,
        BigDecimal price,
        AuthorInput authorInput) {
}

