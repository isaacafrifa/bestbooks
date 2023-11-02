package iam.bestbooks.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Rating {
    NO_STAR("🫣"),
    ONE_STAR("⭐️"),
    TWO_STAR("⭐️️⭐️"),
    THREE_STAR("⭐️⭐️⭐️");

    private final String star;

    Rating(String star){
        this.star= star;
    }

    @JsonValue
    public String getStar(){
        return star;

    }

}
