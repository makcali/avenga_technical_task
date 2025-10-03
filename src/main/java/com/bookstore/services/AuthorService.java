package com.bookstore.services;

import com.bookstore.client.ApiClient;
import com.bookstore.constants.EndPoints;
import com.bookstore.models.Author;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class AuthorService {


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


    @Step("Get author by ID: {authorId}")
    public Response getAuthorById(int authorId) {
        log.info("Fetching author with ID: {}", authorId);
        return ApiClient.getRequestSpec()
                .pathParam("id", authorId)
                .when()
                .get(EndPoints.AUTHORS_BY_ID);
    }


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

    public Author extractAuthor(Response response) {
        return response.as(Author.class);
    }

    public List<Author> extractAuthors(Response response) {
        return Arrays.asList(response.as(Author[].class));
    }
}