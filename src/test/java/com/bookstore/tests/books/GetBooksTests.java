package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Book;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test class for GET Books API endpoints.
 * Covers happy path, edge cases, and negative scenarios.
 *
 * @author API Automation Team
 * @version 1.0
 */

@Slf4j
@Epic("Bookstore API")
@Feature("Books API")
@Story("GET Books")
public class GetBooksTests extends BaseTest {

    /**
     * Test: Get all books returns successful response with non-empty list
     * Priority: Critical - Smoke test
     */
    @Test(description = "Verify get all books returns 200 and non-empty list",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that GET /Books returns successful response with books list")
    public void testGetAllBooksReturnsSuccess() {
        logStep("Send GET request to retrieve all books");
        Response response = bookService.getAllBooks();

        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 OK")
                .isEqualTo(StatusCodes.OK);

        logStep("Verify response contains books list");
        List<Book> books = bookService.extractBooks(response);
        assertThat(books)
                .as("Books list should not be null or empty")
                .isNotNull()
                .isNotEmpty();

        logStep("Verify first book has required fields");
        Book firstBook = books.get(0);
        assertThat(firstBook.getId())
                .as("Book ID should not be null")
                .isNotNull();
        assertThat(firstBook.getTitle())
                .as("Book title should not be null or empty")
                .isNotNull()
                .isNotEmpty();

        log.info("✓ Test passed: Found {} books", books.size());
    }

    /**
     * Test: Validate response structure using Hamcrest matchers
     * Priority: Normal - Regression test
     */
    @Test(description = "Verify get all books response structure using Hamcrest",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates the JSON response structure and data types")
    public void testGetAllBooksResponseStructure() {
        logStep("Send GET request and validate response structure");
        bookService.getAllBooks()
                .then()
                .assertThat()
                .statusCode(StatusCodes.OK)
                .contentType("application/json")
                .body("$", hasSize(greaterThan(0)))
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue())
                .body("[0].pageCount", hasSize(greaterThan(0)));

        log.info("✓ Test passed: Response structure is valid");
    }

    /**
     * Test: Get book by valid ID returns correct book
     * Priority: Critical - Smoke test
     */
    @Test(description = "Verify get book by valid ID returns correct book",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates retrieval of a specific book by ID")
    public void testGetBookByIdReturnsCorrectBook() {
        int bookId = 1;

        logStep("Send GET request for book ID: " + bookId);
        Response response = bookService.getBookById(bookId);

        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 OK")
                .isEqualTo(StatusCodes.OK);

        logStep("Verify returned book has correct ID");
        Book book = bookService.extractBook(response);
        assertThat(book.getId())
                .as("Returned book should have ID: " + bookId)
                .isEqualTo(bookId);

        logStep("Verify book has all required fields populated");
        assertThat(book.getTitle()).as("Title should not be null").isNotNull();
        assertThat(book.getPageCount()).as("Page count should not be null").isNotNull();

        log.info("✓ Test passed: Retrieved book '{}' with ID {}", book.getTitle(), bookId);
    }

    /**
     * Test: Get book with non-existent ID returns 404
     * Priority: Normal - Edge case test
     */
    @Test(description = "Verify get book with non-existent ID returns 404",
            groups = {"regression", "edge-case", "negative"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates proper error handling for non-existent book IDs")
    public void testGetBookByNonExistentIdReturns404() {
        int nonExistentId = getNonExistentBookId();

        logStep("Send GET request for non-existent book ID: " + nonExistentId);
        Response response = bookService.getBookById(nonExistentId);

        logStep("Verify response status code is 404");
        assertThat(response.getStatusCode())
                .as("Status code should be 404 Not Found")
                .isEqualTo(StatusCodes.NOT_FOUND);

        log.info("✓ Test passed: Got 404 for non-existent ID {}", nonExistentId);
    }

    /**
     * Test: Get book with invalid (negative) ID returns 404
     * Priority: Normal - Negative test
     */
    @Test(description = "Verify get book with invalid ID (negative) returns 404",
            groups = {"regression", "edge-case", "negative"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling for invalid negative book IDs")
    public void testGetBookByInvalidIdReturns404() {
        int invalidId = getInvalidBookId();

        logStep("Send GET request for invalid book ID: " + invalidId);
        Response response = bookService.getBookById(invalidId);

        logStep("Verify response status code is 404");
        assertThat(response.getStatusCode())
                .as("Status code should be 404 for invalid ID")
                .isEqualTo(StatusCodes.NOT_FOUND);

        log.info("✓ Test passed: Got 404 for invalid ID {}", invalidId);
    }

    /**
     * Test: Get book with zero ID returns 404
     * Priority: Minor - Edge case test
     */
    @Test(description = "Verify get book with zero ID returns 404",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.MINOR)
    @Description("Validates error handling for zero as book ID")
    public void testGetBookByZeroIdReturns404() {
        int zeroId = 0;

        logStep("Send GET request for book ID: " + zeroId);
        Response response = bookService.getBookById(zeroId);

        logStep("Verify response status code is 404");
        assertThat(response.getStatusCode())
                .as("Status code should be 404 for zero ID")
                .isEqualTo(StatusCodes.NOT_FOUND);

        log.info("✓ Test passed: Got 404 for zero ID");
    }

    /**
     * Test: Verify response time for get all books
     * Priority: Normal - Performance test
     */
    @Test(description = "Verify response time for get all books is acceptable",
            groups = {"regression", "performance"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that API response time is within acceptable limits")
    public void testGetAllBooksResponseTime() {
        logStep("Send GET request and measure response time");
        Response response = bookService.getAllBooks();

        logStep("Verify response time is less than 3 seconds");
        long responseTime = response.getTime();
        assertThat(responseTime)
                .as("Response time should be less than 3000ms")
                .isLessThan(3000L);

        log.info("✓ Test passed: Response time {} ms", responseTime);
    }

    /**
     * Test: Verify all books have consistent data structure
     * Priority: Normal - Data validation test
     */
    @Test(description = "Verify get all books returns consistent data structure",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that all books in the response have consistent structure")
    public void testGetAllBooksDataConsistency() {
        logStep("Send GET request to retrieve all books");
        List<Book> books = bookService.extractBooks(bookService.getAllBooks());

        logStep("Verify all books have consistent structure");
        books.forEach(book -> {
            assertThat(book.getId())
                    .as("Each book should have an ID")
                    .isNotNull();
            assertThat(book.getTitle())
                    .as("Each book should have a title")
                    .isNotNull();
            assertThat(book.getPageCount())
                    .as("Each book should have page count")
                    .isNotNull();
        });

        log.info("✓ Test passed: All {} books have consistent structure", books.size());
    }

    /**
     * Test: Verify books are returned in order by ID
     * Priority: Trivial - Data ordering test
     */
    @Test(description = "Verify books are returned in order by ID",
            groups = {"regression"},
            priority = 3)
    @Severity(SeverityLevel.TRIVIAL)
    @Description("Validates that books are returned in ascending order by ID")
    public void testGetAllBooksOrderedById() {
        logStep("Send GET request to retrieve all books");
        List<Book> books = bookService.extractBooks(bookService.getAllBooks());

        logStep("Verify books are ordered by ID");
        for (int i = 0; i < books.size() - 1; i++) {
            assertThat(books.get(i).getId())
                    .as("Books should be ordered by ID")
                    .isLessThan(books.get(i + 1).getId());
        }

        log.info("✓ Test passed: Books are properly ordered by ID");
    }

    /**
     * Test: Verify content type header in response
     * Priority: Minor - Header validation test
     */
    @Test(description = "Verify content type header is application/json",
            groups = {"regression"},
            priority = 3)
    @Severity(SeverityLevel.MINOR)
    @Description("Validates that the API returns correct content type header")
    public void testGetAllBooksContentType() {
        logStep("Send GET request and verify content type");
        Response response = bookService.getAllBooks();

        logStep("Verify content type is application/json");
        assertThat(response.getContentType())
                .as("Content type should be application/json")
                .contains("application/json");

        log.info("✓ Test passed: Content type is correct");
    }

    @Test(description = "Verify IDs are unique in list",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.MINOR)
    public void testIdsAreUnique() {
        List<Book> books = bookService.extractBooks(bookService.getAllBooks());
        long distinct = books.stream().map(Book::getId).distinct().count();
        assertThat(distinct).isEqualTo(books.size());
    }
}