package iam.bestbooks.model;

public record Author (
        Integer id,
        String firstName,
        String lastName
){

    public String getFullName(){
        return "%s %s".formatted(firstName, lastName);
    }
}
