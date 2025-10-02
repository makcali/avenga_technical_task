package com.bookstore.services;

import com.bookstore.client.ApiClient;
import com.bookstore.constants.EndPoints;
import com.bookstore.models.Author;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Service class for Author API operations.
 * Encapsulates all author-related API calls.
 *
 * @author API Automation Team
 * @version 1.0
 */
@Slf4j
public class AuthorService {

    /**
     * Retrieves all authors from the API.
     * GET /api/v1/Authors
     *
     * @return Response object containing all authors
     */
    @Step("Get all authors")
    public Response getAllAuthors() {
        log.info("Fetching all authors");
        return ApiClient.getRequestSpec()
                .when()
                .get(EndPoints.AUTHORS)
                .then()
                .extract()
                .response();
    }

    /**
     * Retrieves a specific author by ID.
     * GET /api/v1/Authors/{id}
     *
     * @param authorId the author identifier
     * @return Response object containing the author
     */
    @Step("Get author by ID: {authorId}")
    public Response getAuthorById(int authorId) {
        log.info("Fetching author with ID: {}", authorId);
        return ApiClient.getRequestSpec()
                .pathParam("id", authorId)
                .when()
                .get(EndPoints.AUTHORS_BY_ID)
                .then()
                .extract()
                .response();
    }

    /**
     * Creates a new author.
     * POST /api/v1/Authors
     *
     * @param author the author object to create
     * @return Response object containing the created author
     */
    @Step("Create new author: {author.firstName} {author.lastName}")
    public Response createAuthor(Author author) {
        log.info("Creating new author: {} {}", author.getFirstName(), author.getLastName());
        return ApiClient.getRequestSpec()
                .body(author)
                .when()
                .post(EndPoints.AUTHORS)
                .then()
                .extract()
                .response();
    }

    /**
     * Updates an existing author.
     * PUT /api/v1/Authors/{id}
     *
     * @param authorId the author identifier
     * @param author the updated author object
     * @return Response object containing the updated author
     */
    @Step("Update author ID {authorId}")
    public Response updateAuthor(int authorId, Author author) {
        log.info("Updating author with ID: {}", authorId);
        author.setId(authorId);
        return ApiClient.getRequestSpec()
                .pathParam("id", authorId)
                .body(author)
                .when()
                .put(EndPoints.AUTHORS_BY_ID)
                .then()
                .extract()
                .response();
    }

    /**
     * Deletes an author by ID.
     * DELETE /api/v1/Authors/{id}
     *
     * @param authorId the author identifier
     * @return Response object
     */
    @Step("Delete author by ID: {authorId}")
    public Response deleteAuthor(int authorId) {
        log.info("Deleting author with ID: {}", authorId);
        return ApiClient.getRequestSpec()
                .pathParam("id", authorId)
                .when()
                .delete(EndPoints.AUTHORS_BY_ID)
                .then()
                .extract()
                .response();
    }

    /**
     * Extracts a single author from response.
     *
     * @param response the API response
     * @return Author object
     */
    public Author extractAuthor(Response response) {
        return response.as(Author.class);
    }

    /**
     * Extracts a list of authors from response.
     *
     * @param response the API response
     * @return List of Author objects
     */
    public List<Author> extractAuthors(Response response) {
        return Arrays.asList(response.as(Author[].class));
    }
}