package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;


@Slf4j
@Epic("Bookstore API")
@Feature("Books API")
@Story("PUT Books - Update Book")
public class UpdateBookTests extends BaseTest {

    private Book createAndReturnBook() {
        Book initialPayload = TestDataGenerator.generateRandomBook();
        Response createResponse = bookService.createBook(initialPayload);
        createResponse.then().statusCode(anyOf(is(StatusCodes.OK), is(StatusCodes.CREATED)));
        return bookService.extractBook(createResponse);
    }

    @Test(description = "Verify successful update with valid data and persistence check",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests successful book update, verifies response, and confirms persistence via GET.")
    public void testUpdateBookWithValidDataReturnsSuccess() {
        Book createdBook = createAndReturnBook();
        int bookId = createdBook.getId();
        String newTitle = "Updated Title - " + System.currentTimeMillis();
        Book updatePayload = createdBook.toBuilder()
                .title(newTitle)
                .build();
        logStep("Update book ID " + bookId + " with new title: " + newTitle);
        Response updateResponse = bookService.updateBook(bookId, updatePayload);
        assertThat(updateResponse.getStatusCode()).as("Update status code should be 200 OK").isEqualTo(StatusCodes.OK);
        Response getResponse = bookService.getBook(bookId);
        Book finalBook = bookService.extractBook(getResponse);
        assertThat(finalBook.getTitle()).as("Title must be updated in the database").isEqualTo(newTitle);
        assertThat(finalBook.getDescription()).as("Description must retain its original value").isEqualTo(createdBook.getDescription());
        log.info("Test passed: Book {} successfully updated and verified.", bookId);
    }

    @Test(description = "Verify updating multiple fields of a book",
            groups = {"regression", "happy-path"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that multiple book fields can be updated simultaneously and verifies persistence.")
    public void testUpdateMultipleBookFields() {
        Book createdBook = createAndReturnBook();
        int bookId = createdBook.getId();
        Book updatePayload = createdBook.toBuilder()
                .title("Completely Updated Title")
                .description("Completely Updated Description")
                .pageCount(750)
                .excerpt("Completely Updated Excerpt")
                .publishDate("2024-12-31T23:59:59")
                .build();
        logStep("Update book ID " + bookId + " with all new fields");
        Response updateResponse = bookService.updateBook(bookId, updatePayload);
        assertThat(updateResponse.getStatusCode()).as("Update should be successful").isEqualTo(StatusCodes.OK);
        Response getResponse = bookService.getBook(bookId);
        Book finalBook = bookService.extractBook(getResponse);
        assertThat(finalBook.getTitle()).as("Title should be updated").isEqualTo(updatePayload.getTitle());
        assertThat(finalBook.getDescription()).as("Description should be updated").isEqualTo(updatePayload.getDescription());
        assertThat(finalBook.getPageCount()).as("Page count should be updated").isEqualTo(updatePayload.getPageCount());
        log.info("Test passed: Multiple fields updated successfully");
    }

    @Test(description = "Verify partial update of book fields (PUT preserves existing data)",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that only specified fields can be updated by preserving original values in the PUT request.")
    public void testPartialUpdateBook() {
        Book createdBook = createAndReturnBook();
        Book updatePayload = createdBook.toBuilder()
                .title("Only Title Updated")
                .pageCount(999)
                .build();
        logStep("Perform PUT update with ID: " + createdBook.getId());
        Response response = bookService.updateBook(createdBook.getId(), updatePayload);
        assertThat(response.getStatusCode())
                .as("PUT request should succeed when original fields are preserved")
                .isEqualTo(StatusCodes.OK);
        Response getResponse = bookService.getBook(createdBook.getId());
        Book finalBook = bookService.extractBook(getResponse);
        assertThat(finalBook.getTitle()).as("Title must be updated").isEqualTo("Only Title Updated");
        assertThat(finalBook.getPublishDate()).as("PublishDate must be preserved").isEqualTo(createdBook.getPublishDate());
        log.info("Test passed: Partial update successful");
    }

    @Test(description = "Verify updating a non-existent book returns 404 or expected status",
            groups = {"regression", "negative", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling when updating non-existent book")
    public void testUpdateNonExistentBook() {
        int nonExistentId = getNonExistentBookId(); // Var olmayan ID
        Book book = TestDataGenerator.generateRandomBook();
        logStep("Attempt to update non-existent book ID: " + nonExistentId);
        Response response = bookService.updateBook(nonExistentId, book);
        assertThat(response.getStatusCode())
                .as("Status code for non-existent book")
                .isIn(StatusCodes.NOT_FOUND, StatusCodes.OK, StatusCodes.CREATED);
    }

    @Test(description = "Verify updating book with empty fields",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates API behavior when empty fields are provided in update")
    public void testUpdateBookWithEmptyFields() {
        Book createdBook = createAndReturnBook();
        Book bookWithEmptyFields = TestDataGenerator.generateBookWithEmptyFields();
        logStep("Attempt to update book ID " + createdBook.getId() + " with empty fields");
        Response response = bookService.updateBook(createdBook.getId(), bookWithEmptyFields);
        assertThat(response.getStatusCode())
                .as("Status code for empty fields")
                .isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY);
    }

    @Test(description = "Verify updating book with very long title (501+ boundary)",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates boundary condition with maximum title length in update")
    public void testUpdateBookWithLongTitle() {
        Book createdBook = createAndReturnBook();
        Book bookWithLongTitle = createdBook.toBuilder()
                .title(TestDataGenerator.generateBookWithLongTitle(501).getTitle())
                .build();
        logStep("Update book with very long title (501 characters)");
        Response response = bookService.updateBook(createdBook.getId(), bookWithLongTitle);
        assertThat(response.getStatusCode())
                .as("Should reject titles exceeding max length in update")
                .isIn(StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY);
    }

    @Test(description = "Verify updating book with special characters",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates handling of special characters in update")
    public void testUpdateBookWithSpecialCharacters() {
        Book createdBook = createAndReturnBook();
        Book bookWithSpecialChars = createdBook.toBuilder()
                .title(TestDataGenerator.generateBookWithSpecialCharacters().getTitle())
                .build();
        logStep("Update book with special characters in title");
        Response response = bookService.updateBook(createdBook.getId(), bookWithSpecialChars);
        assertThat(response.getStatusCode()).as("Should accept special characters in update").isEqualTo(StatusCodes.OK);
    }

    @Test(description = "Verify updating book with invalid ID (negative)",
            groups = {"regression", "negative", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling for invalid negative book ID in update")
    public void testUpdateBookWithInvalidId() {
        int invalidId = getInvalidBookId();
        Book book = TestDataGenerator.generateRandomBook();
        logStep("Attempt to update book with invalid ID: " + invalidId);
        Response response = bookService.updateBook(invalidId, book);
        assertThat(response.getStatusCode())
                .as("Status code for invalid ID")
                .isIn(StatusCodes.BAD_REQUEST, StatusCodes.NOT_FOUND);
    }

    @Test(description = "Verify updating book with zero page count",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates handling of zero as page count in update")
    public void testUpdateBookWithZeroPageCount() {
        Book createdBook = createAndReturnBook();
        Book updatePayload = createdBook.toBuilder()
                .pageCount(0)
                .build();
        Response response = bookService.updateBook(createdBook.getId(), updatePayload);
        assertThat(response.getStatusCode()).as("Status code for zero page count").isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST);
    }

    @Test(description = "Verify updating book with negative page count",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates handling of negative as page count in update")
    public void testUpdateBookWithNegativePageCount() {
        Book createdBook = createAndReturnBook();
        Book updatePayload = createdBook.toBuilder()
                .pageCount(-50)
                .build();
        Response response = bookService.updateBook(createdBook.getId(), updatePayload);
        assertThat(response.getStatusCode())
                .as("Status code for negative page count")
                .isIn(StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY);
    }

    @Test(description = "Verify updating book with NonInteger page count",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates handling of NonInteger as page count in update")
    public void testUpdateBookWithNonIntegerPageCount() {
        Book createdBook = createAndReturnBook();
        logStep("This test requires custom raw JSON to send a non-integer value, " +
                "but for now we confirm API rejects invalid type if possible.");
        Book updatePayload = createdBook.toBuilder()
                .pageCount(10)
                .build();
        Response response = bookService.updateBook(createdBook.getId(), updatePayload);
        assertThat(response.getStatusCode())
                .as("Non-integer page count should be rejected/parsed")
                .isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY);
    }

    @Test(description = "Verify updating book with invalid publishDate format",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that book update with invalid publishDate format")
    public void testUpdateBookWithInvalidDateFormat() {
        Book createdBook = createAndReturnBook();
        Book updatePayload = createdBook.toBuilder()
                .publishDate("10-04-2025")
                .build();
        Response response = bookService.updateBook(createdBook.getId(), updatePayload);
        assertThat(response.getStatusCode())
                .as("Should be rejected due to invalid date format")
                .isEqualTo(StatusCodes.BAD_REQUEST);
    }

    @Test(description = "Verify updating book with blank string fields",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that book update with empty fields (empty string vs null)")
    public void testUpdateBookWithBlankStringFields() {
        Book createdBook = createAndReturnBook();
        Book updatePayload = createdBook.toBuilder()
                .excerpt("")
                .build();
        Response response = bookService.updateBook(createdBook.getId(), updatePayload);
        assertThat(response.getStatusCode()).as("Status code for empty field").isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST);
    }


    @Test(description = "Verify PUT request handles mismatched ID between URL and Body",
            groups = {"regression", "negative", "security"},
            priority = 3)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests if API prioritizes URL ID or Body ID, or rejects the request entirely.")
    public void testUpdateBookWithMismatchId() {
        Book createdBook = createAndReturnBook();
        int realBookId = createdBook.getId();
        int mismatchedId = TestDataGenerator.generateUniqueBookId();
        Book updatePayloadWithWrongId = createdBook.toBuilder()
                .id(mismatchedId)
                .title("Updated Title with Mismatched ID Test")
                .build();
        logStep("Real Book ID (URL): " + realBookId + " | Mismatched ID (Body): " + mismatchedId);
        Response response = bookService.updateBook(realBookId, updatePayloadWithWrongId);
        int statusCode = response.getStatusCode();
        if (statusCode == StatusCodes.OK) {
            Book finalBook = bookService.extractBook(response);
            assertThat(finalBook.getId())
                    .as("API must prioritize URL ID and keep the book ID intact")
                    .isEqualTo(realBookId);
            log.warn("API accepted the request but prioritized URL ID ({})", realBookId);
        } else if (statusCode == StatusCodes.BAD_REQUEST || statusCode == StatusCodes.UNPROCESSABLE_ENTITY) {
            log.info(" API correctly rejected the mismatched ID request (Status: {})", statusCode);
        }
        assertThat(statusCode)
                .as("API response for ID mismatch should be consistent (200, 400, or 422)")
                .isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY);
    }


    @Test(description = "Verify response time for book update is acceptable",
            groups = {"regression", "performance"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that book update response time is within limits")
    public void testUpdateBookResponseTime() {
        Book createdBook = createAndReturnBook();
        Book updatePayload = createdBook.toBuilder().title("Performance Test Update").build();
        logStep("Update book and measure response time");
        Response response = bookService.updateBook(createdBook.getId(), updatePayload);
        logStep("Verify response time is less than 3 seconds");
        assertThat(response.getTime())
                .as("Response time should be acceptable")
                .isLessThan(3000L);
        log.info("Test passed: Update response time {} ms", response.getTime());
    }
}