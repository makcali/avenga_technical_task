package com.bookstore.services;

import com.bookstore.client.ApiClient;
import com.bookstore.constants.EndPoints;
import com.bookstore.models.Book;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Service class for Book API operations.
 * Encapsulates all book-related API calls.
 * Uses Service Layer pattern to abstract API logic from tests.
 *
 * @author API Automation Team
 * @version 1.0
 */
@Slf4j
public class BookService {

    /**
     * Retrieves all books from the API.
     * GET /api/v1/Books
     *
     * @return Response object containing all books
     */
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

    /**
     * Retrieves a specific book by ID.
     * GET /api/v1/Books/{id}
     *
     * @param bookId the book identifier
     * @return Response object containing the book
     */
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

    /**
     * Creates a new book.
     * POST /api/v1/Books
     *
     * @param book the book object to create
     * @return Response object containing the created book
     */
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

    /**
     * Updates an existing book.
     * PUT /api/v1/Books/{id}
     *
     * @param bookId the book identifier
     * @param book the updated book object
     * @return Response object containing the updated book
     */
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

    /**
     * Deletes a book by ID.
     * DELETE /api/v1/Books/{id}
     *
     * @param bookId the book identifier
     * @return Response object
     */
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

    /**
     * Extracts a single book from response.
     *
     * @param response the API response
     * @return Book object
     */
    public Book extractBook(Response response) {
        return response.as(Book.class);
    }

    /**
     * Extracts a list of books from response.
     *
     * @param response the API response
     * @return List of Book objects
     */
    public List<Book> extractBooks(Response response) {
        return Arrays.asList(response.as(Book[].class));
    }

    /**
     * Verifies if a book exists by ID.
     *
     * @param bookId the book identifier
     * @return true if book exists, false otherwise
     */
    @Step("Verify book exists: {bookId}")
    public boolean bookExists(int bookId) {
        Response response = getBookById(bookId);
        return response.getStatusCode() == 200;
    }
}