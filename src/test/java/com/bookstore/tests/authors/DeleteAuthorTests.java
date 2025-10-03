package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;


@Slf4j
@Epic("Bookstore API")
@Feature("Authors API")
@Story("DELETE Authors")
public class DeleteAuthorTests extends BaseTest {

    @Test(description = "Verify deleting an author by valid ID returns 200",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify successful author deletion with valid ID")
    public void testDeleteAuthorWithValidIdReturnsSuccess() {
        int authorId = TestDataGenerator.generateRandomAuthor().getId();
        logStep("Delete author with ID: " + authorId);
        Response response = authorService.deleteAuthor(authorId);
        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 OK")
                .isEqualTo(StatusCodes.OK);
        log.info("Test passed: Author {} deleted successfully", authorId);
    }

    @Test(description = "Verify deleting multiple authors sequentially",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that multiple authors can be deleted in sequence")
    public void testDeleteMultipleAuthorsSuccessfully() {
        logStep("Delete 5 authors sequentially");

        for (int i = 1; i <= 5; i++) {
            int authorId = TestDataGenerator.generateRandomAuthor().getId();
            logStep("Delete author " + i + " with ID: " + authorId);
            Response response = authorService.deleteAuthor(authorId);
            assertThat(response.getStatusCode())
                    .as("Each deletion should return 200")
                    .isEqualTo(StatusCodes.OK);
        }
        log.info("Test passed: Successfully deleted 5 authors");
    }


    @Test(description = "Verify deleting a non-existent author",
            groups = {"regression", "negative", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling when deleting non-existent author")
    public void testDeleteNonExistentAuthor() {
        int nonExistentId = getNonExistentBookId();
        logStep("Attempt to delete non-existent author ID: " + nonExistentId);
        Response response = authorService.deleteAuthor(nonExistentId);
        logStep("Verify response for non-existent author");
        assertThat(response.getStatusCode())
                .as("Status code for non-existent author")
                .isIn(StatusCodes.NOT_FOUND);
        log.info("Test passed: Non-existent author response = {}", response.getStatusCode());
    }


    @Test(description = "Verify deleting author with invalid ID (negative)",
            groups = {"regression", "negative", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling for invalid negative author ID")
    public void testDeleteAuthorWithInvalidId() {
        int invalidId = getInvalidBookId();
        logStep("Attempt to delete author with invalid ID: " + invalidId);
        Response response = authorService.deleteAuthor(invalidId);
        logStep("Verify response for invalid ID");
        assertThat(response.getStatusCode())
                .as("Status code for invalid ID")
                .isIn(StatusCodes.BAD_REQUEST, StatusCodes.NOT_FOUND);
        log.info("Test passed: Invalid ID response = {}", response.getStatusCode());
    }

    @Test(description = "Verify deleting author with zero ID",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.MINOR)
    @Description("Validates error handling for zero as author ID")
    public void testDeleteAuthorWithZeroId() {
        int zeroId = 0;
        logStep("Attempt to delete author with ID 0");
        Response response = authorService.deleteAuthor(zeroId);
        logStep("Verify response for zero ID");
        assertThat(response.getStatusCode())
                .as("Status code for zero ID")
                .isIn(StatusCodes.BAD_REQUEST, StatusCodes.NOT_FOUND);
        log.info("Test passed: Zero ID response = {}", response.getStatusCode());
    }


    @Test(description = "Verify response time for author deletion is acceptable",
            groups = {"regression", "performance"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that author deletion response time is within limits")
    public void testDeleteAuthorResponseTime() {
        int authorId = TestDataGenerator.generateRandomAuthor().getId();
        logStep("Delete author and measure response time");
        Response response = authorService.deleteAuthor(authorId);
        logStep("Verify response time is less than 3 seconds");
        assertThat(response.getTime())
                .as("Response time should be acceptable")
                .isLessThan(3000L);
        log.info("Test passed: Delete response time {} ms", response.getTime());
    }

    @Test(description = "Verify complete CRUD workflow for author with immediate read check",
            groups = {"regression", "integration"},
            priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates Create-Read-Update-Delete workflow, ensuring data persistence immediately after creation.")
    public void testCompleteAuthorCRUDWorkflow(){
        logStep("Step 1: Create a new author");
        Author newAuthor = TestDataGenerator.generateRandomAuthor();
        Response createResponse = authorService.createAuthor(newAuthor);
        assertThat(createResponse.getStatusCode()).as("Create status code").isIn(StatusCodes.OK, StatusCodes.CREATED);
        Author createdAuthor = authorService.extractAuthor(createResponse);
        int authorId = createdAuthor.getId();
        log.info("Created author with ID: {}", authorId);
        logStep("Step 2: Retrieve the created author and verify content");
        await()
                .atMost(10, SECONDS)
                .pollInterval(500, MILLISECONDS)
                .ignoreExceptions()
                .until(() -> {
                    Response response = authorService.getAuthorById(authorId);
                    return response.getStatusCode() == 200;
                });
        Response getResponse = authorService.getAuthorById(authorId);
        Author retrievedAuthor = authorService.extractAuthor(getResponse);
        log.info("📢 Retrieved Author Object: {}", retrievedAuthor);
        assertThat(retrievedAuthor.getFirstName())
                .as("Retrieved First Name should match created value.")
                .isEqualTo(createdAuthor.getFirstName());
        assertThat(retrievedAuthor.getIdBook())
                .as("Retrieved IdBook should match created value.")
                .isEqualTo(createdAuthor.getIdBook());
        log.info("Verification successful: Author found and data integrity confirmed.");
        logStep("Step 3: Update the author's first name");
        Author updatePayload = createdAuthor.toBuilder()
                .firstName("Updated_" + createdAuthor.getFirstName())
                .build();
        Response updateResponse = authorService.updateAuthor(authorId, updatePayload);
        assertThat(updateResponse.getStatusCode()).as("Update status code").isEqualTo(StatusCodes.OK);
        logStep("Verify update persistence by reading again");
        Response postUpdateGet = authorService.getAuthorById(authorId);
        Author updatedAuthor = authorService.extractAuthor(postUpdateGet);
        assertThat(updatedAuthor.getFirstName()).as("First name must be updated").isEqualTo(updatePayload.getFirstName());
        log.info("Updated author with ID: {}", authorId);
        logStep("Step 4: Delete the author");
        Response deleteResponse = authorService.deleteAuthor(authorId);
        assertThat(deleteResponse.getStatusCode()).as("Delete status code").isEqualTo(StatusCodes.OK);
        logStep("Step 5: Verify deletion (Author should return 404)");
        Response finalGetResponse = authorService.getAuthorById(authorId);
        assertThat(finalGetResponse.getStatusCode()).as("Status after deletion should be 404").isEqualTo(StatusCodes.NOT_FOUND);
        log.info("Test passed: Complete author CRUD workflow successful and verified.");
    }

    @Test(description = "Verify deleting same author twice",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates idempotency of delete operation")
    public void testDeleteSameAuthorTwice() {
        int authorId = getRandomAuthorId();
        logStep("First deletion of author ID: " + authorId);
        Response firstResponse = authorService.deleteAuthor(authorId);
        assertThat(firstResponse.getStatusCode()).isEqualTo(StatusCodes.OK);
        logStep("Second deletion of same author ID: " + authorId);
        Response secondResponse = authorService.deleteAuthor(authorId);
        logStep("Verify second deletion response");
        assertThat(secondResponse.getStatusCode())
                .as("Second deletion may return 200 or 404")
                .isIn(StatusCodes.OK, StatusCodes.NOT_FOUND);
        log.info("Test passed: Idempotency validated");
    }
}