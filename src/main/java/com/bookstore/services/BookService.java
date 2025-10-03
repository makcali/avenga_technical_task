package com.bookstore.services;

import com.bookstore.client.ApiClient;
import com.bookstore.constants.EndPoints;
import com.bookstore.models.Book;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;


@Slf4j
public class BookService {


    @Step("Get all books")
    public Response getAllBooks() {
        log.info("Fetching all books");
        return ApiClient.getRequestSpec()
                .when()
                .get(EndPoints.BOOKS)
                .then()
                .extract()
                .response();
    }

    @Step("Get book by ID: {bookId}")
    public Response getBookById(int bookId) {
        log.info("Fetching book with ID: {}", bookId);
        return ApiClient.getRequestSpec()
                .pathParam("id", bookId)
                .when()
                .get(EndPoints.BOOKS_BY_ID)
                .then()
                .extract()
                .response();
    }

    @Step("Create new book: {book.title}")
    public Response createBook(Book book) {
        log.info("Creating new book: {}", book.getTitle());
        return ApiClient.getRequestSpec()
                .body(book)
                .when()
                .post(EndPoints.BOOKS)
                .then()
                .extract()
                .response();
    }

    @Step("Update book ID {bookId} with title: {book.title}")
    public Response updateBook(int bookId, Book book) {
        log.info("Updating book with ID: {} to title: {}", bookId, book.getTitle());
        book.setId(bookId); // Ensure ID matches
        return ApiClient.getRequestSpec()
                .pathParam("id", bookId)
                .body(book)
                .when()
                .put(EndPoints.BOOKS_BY_ID)
                .then()
                .extract()
                .response();
    }

    @Step("Delete book by ID: {bookId}")
    public Response deleteBook(int bookId) {
        log.info("Deleting book with ID: {}", bookId);
        return ApiClient.getRequestSpec()
                .pathParam("id", bookId)
                .when()
                .delete(EndPoints.BOOKS_BY_ID)
                .then()
                .extract()
                .response();
    }

    public Book extractBook(Response response) {
        return response.as(Book.class);
    }

    public Response getBook(int bookId) {
        return ApiClient.getRequestSpec()
                .pathParam("id", bookId)
                .when()
                .get(EndPoints.BOOKS_BY_ID);
    }

    public List<Book> extractBooks(Response response) {
        return Arrays.asList(response.as(Book[].class));
    }

}