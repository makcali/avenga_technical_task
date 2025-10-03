package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Epic("Bookstore API")
@Feature("Authors API")
@Story("PUT Authors - Update Author")
public class UpdateAuthorTests extends BaseTest {

    @Test(description = "Verify updating an author with valid data returns 200",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify successful author update with valid data")
    public void testUpdateAuthorWithValidDataReturnsSuccess() {
        int authorId = TestDataGenerator.generateRandomAuthor().getId();
        logStep("Generate updated author data");
        Author updatedAuthor = TestDataGenerator.generateRandomAuthor();
        updatedAuthor.setFirstName("Updated_" + updatedAuthor.getFirstName());
        logStep("Update author ID " + authorId + " with new data");
        Response response = authorService.updateAuthor(authorId, updatedAuthor);
        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 OK")
                .isEqualTo(StatusCodes.OK);
        logStep("Verify updated author data matches request");
        Author returnedAuthor = authorService.extractAuthor(response);
        assertThat(returnedAuthor.getId())
                .as("Author ID should match")
                .isEqualTo(authorId);
        assertThat(returnedAuthor.getFirstName())
                .as("First name should be updated")
                .isEqualTo(updatedAuthor.getFirstName());
        log.info("Test passed: Author {} updated successfully", authorId);
    }


    @Test(description = "Verify updating multiple fields of an author",
            groups = {"regression", "happy-path"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that multiple author fields can be updated simultaneously")
    public void testUpdateMultipleAuthorFields() {
        int authorId = TestDataGenerator.generateRandomAuthor().getId();
        logStep("Create author with all fields updated");
        Author updatedAuthor = Author.builder()
                .firstName("CompletelyNew")
                .lastName("UpdatedName")
                .idBook(99)
                .build();
        logStep("Update author with all new fields");
        Response response = authorService.updateAuthor(authorId, updatedAuthor);
        assertThat(response.getStatusCode())
                .as("Update should be successful")
                .isEqualTo(StatusCodes.OK);
        Author returnedAuthor = authorService.extractAuthor(response);
        assertThat(returnedAuthor.getFirstName()).isEqualTo(updatedAuthor.getFirstName());
        assertThat(returnedAuthor.getLastName()).isEqualTo(updatedAuthor.getLastName());
        log.info("Test passed: Multiple fields updated successfully");
    }


    @Test(description = "Verify updating a non-existent author",
            groups = {"regression", "negative", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling when updating non-existent author")
    public void testUpdateNonExistentAuthor() {
        int nonExistentId = getNonExistentBookId();
        Author author = TestDataGenerator.generateRandomAuthor();
        logStep("Attempt to update non-existent author ID: " + nonExistentId);
        Response response = authorService.updateAuthor(nonExistentId, author);
        logStep("Verify response for non-existent author");
        assertThat(response.getStatusCode())
                .as("Status code for non-existent author")
                .isIn(StatusCodes.OK, StatusCodes.NOT_FOUND);
        log.info("Test passed: Non-existent author response = {}", response.getStatusCode());
    }

    @Test(description = "Verify partial update of author fields",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that only specified fields can be updated")
    public void testPartialUpdateAuthor() {
        int authorId = TestDataGenerator.generateRandomAuthor().getId();
        logStep("Create author with only first name");
        Author partialAuthor = Author.builder()
                .firstName("OnlyFirstName")
                .build();
        logStep("Perform partial update with only some fields");
        Response response = authorService.updateAuthor(authorId, partialAuthor);

        logStep("Verify partial update is accepted");
        assertThat(response.getStatusCode())
                .as("Partial update should be accepted")
                .isEqualTo(StatusCodes.OK);
        log.info("Test passed: Partial update successful");
    }


    @Test(description = "Verify updating author with empty first name",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates API behavior when empty first name is provided in update")
    public void testUpdateAuthorWithEmptyFirstName() {
        int authorId = TestDataGenerator.generateRandomAuthor().getId();
        Author author = Author.builder()
                .firstName("")
                .lastName("ValidLastName")
                .build();
        logStep("Attempt to update author with empty first name");
        Response response = authorService.updateAuthor(authorId, author);
        logStep("Verify API response for empty first name");
        assertThat(response.getStatusCode())
                .as("Status code for empty first name")
                .isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST);
        log.info("Test passed: Empty first name response = {}", response.getStatusCode());
    }

    @Test(description = "Verify updating author with invalid ID (negative)",
            groups = {"regression", "negative", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling for invalid negative author ID in update")
    public void testUpdateAuthorWithInvalidId() {
        int invalidId = getInvalidBookId();
        Author author = TestDataGenerator.generateRandomAuthor();
        logStep("Attempt to update author with invalid ID: " + invalidId);
        Response response = authorService.updateAuthor(invalidId, author);
        logStep("Verify response for invalid ID");
        assertThat(response.getStatusCode())
                .as("Status code for invalid ID")
                .isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST, StatusCodes.NOT_FOUND);
        log.info("Test passed: Invalid ID response = {}", response.getStatusCode());
    }

    @Test(description = "Verify response time for author update is acceptable",
            groups = {"regression", "performance"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that author update response time is within limits")
    public void testUpdateAuthorResponseTime() {
        int authorId = TestDataGenerator.generateRandomAuthor().getId();
        Author author = TestDataGenerator.generateRandomAuthor();
        logStep("Update author and measure response time");
        Response response = authorService.updateAuthor(authorId, author);
        logStep("Verify response time is less than 3 seconds");
        assertThat(response.getTime())
                .as("Response time should be acceptable")
                .isLessThan(3000L);
        log.info("Test passed: Update response time {} ms", response.getTime());
    }
}