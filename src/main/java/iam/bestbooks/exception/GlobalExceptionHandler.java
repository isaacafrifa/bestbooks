package iam.bestbooks.exception;

import graphql.GraphQLError;
import graphql.language.SourceLocation;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @GraphQlExceptionHandler
    public GraphQLError handleResourceNotFoundException(ResourceNotFound ex, DataFetchingEnvironment environment) {
        // Get line and column information from the DataFetchingEnvironment
        SourceLocation sourceLocation = environment.getField().getSourceLocation();
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("code", "RESOURCE_NOT_FOUND");

        log.error(ex.getMessage());
        return GraphQLError.newError()
                .message(ex.getMessage())
                .location(sourceLocation)
                .extensions(extensions)
                .build();
    }

    @GraphQlExceptionHandler
    public GraphQLError handleGenericException(Exception ex, DataFetchingEnvironment environment) {
        // Get line and column information from the DataFetchingEnvironment
        SourceLocation sourceLocation = environment.getField().getSourceLocation();

        log.error("An unexpected error occurred: {}", ex.getLocalizedMessage());
        return GraphQLError.newError()
                .message("An unexpected error occurred")
                .location(sourceLocation)
                .build();
    }

}
