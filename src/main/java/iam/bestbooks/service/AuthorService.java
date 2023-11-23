package iam.bestbooks.service;

import iam.bestbooks.dto.AuthorInput;
import iam.bestbooks.entity.Author;
import iam.bestbooks.exception.ResourceNotFound;
import iam.bestbooks.repository.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public record AuthorService(AuthorRepository authorRepository) {

    public Author findOneAuthor(Integer authorId){
        return authorRepository.findById(authorId)
                .orElseThrow(
                        ()-> new ResourceNotFound("Author not found")
                );
    }

    public Author addAuthor(AuthorInput authorInput){
        var authorToSave = new Author(null,
                authorInput.firstName(),
                authorInput.lastName()
        );
       return authorRepository.save(authorToSave);
    }

    public Author updateAuthor(Integer authorId, AuthorInput authorInput){
        var found = findOneAuthor(authorId);
        found.setFirstName(authorInput.firstName());
        found.setLastName(authorInput.lastName());
        return authorRepository.save(found);
    }

}
