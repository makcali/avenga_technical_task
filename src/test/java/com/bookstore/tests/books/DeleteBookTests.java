package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.client.ApiClient;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import java.time.Duration;
import static org.awaitility.Awaitility.await;

@Slf4j
@Epic("Bookstore API")
@Feature("Books API")
@Story("DELETE Books")
public class DeleteBookTests extends BaseTest {

    @Test(description = "Delete a random existing book; verify per env behavior",
            groups = {"smoke","regression"}, priority = 1)
    public void testDeleteRandomExistingBookReturnsSuccess() {
        List<Book> books = bookService.extractBooks(bookService.getAllBooks());
        int bookId = books.get(ThreadLocalRandom.current().nextInt(books.size())).getId();
        Response deleteResp = bookService.deleteBook(bookId);
        deleteResp.then().statusCode(anyOf(is(StatusCodes.OK), is(StatusCodes.NO_CONTENT)));
        boolean expectPersistence =
                config.deletionPersistence() &&
                        !ApiClient.getBaseUrl().contains("fakerestapi.azurewebsites.net");
        if (expectPersistence) {
            await().atMost(Duration.ofSeconds(3))
                    .pollInterval(Duration.ofMillis(200))
                    .untilAsserted(() ->
                            bookService.getBookById(bookId).then().statusCode(StatusCodes.NOT_FOUND)
                    );
        } else {
            log.warn("Sandbox/non-persistent environment: skipping 404 verification after DELETE");
        }
    }

    @Test(description = "Verify deleting multiple books sequentially",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that multiple books can be deleted in sequence")
    public void testDeleteMultipleBooksSuccessfully() {
        logStep("Delete 5 books sequentially");
        for (int i = 1; i <= 5; i++) {
            int bookId = getRandomBookId();
            logStep("Delete book " + i + " with ID: " + bookId);
            Response response = bookService.deleteBook(bookId);
            assertThat(response.getStatusCode())
                    .as("Each deletion should return 200")
                    .isEqualTo(StatusCodes.OK);
        }
        log.info("Test passed: Successfully deleted 5 books");
    }

    @Test(description = "Verify deleting a non-existent book",
            groups = {"regression", "negative", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling when deleting non-existent book")
    public void testDeleteNonExistentBook() {
        int nonExistentId = getNonExistentBookId();
        logStep("Attempt to delete non-existent book ID: " + nonExistentId);
        Response response = bookService.deleteBook(nonExistentId);
        logStep("Verify response for non-existent book");
        assertThat(response.getStatusCode())
                .as("Status code for non-existent book")
                .isIn(StatusCodes.OK, StatusCodes.NOT_FOUND);
        log.info("Test passed: Non-existent book response = {}", response.getStatusCode());
    }


    @Test(description = "Verify deleting book with invalid ID (negative)",
            groups = {"regression", "negative", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling for invalid negative book ID")
    public void testDeleteBookWithInvalidId() {
        int invalidId = getInvalidBookId();
        logStep("Attempt to delete book with invalid ID: " + invalidId);
        Response response = bookService.deleteBook(invalidId);
        logStep("Verify response for invalid ID");
        assertThat(response.getStatusCode())
                .as("Status code for invalid ID")
                .isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST, StatusCodes.NOT_FOUND);
        log.info("Test passed: Invalid ID response = {}", response.getStatusCode());
    }
}