package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Author;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
@Epic("Bookstore API")
@Feature("Authors API")
@Story("POST Authors - Create Author")
public class CreateAuthorTests extends BaseTest {


    @Test(description = "Verify creating an author with valid data returns 200",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test to verify successful author creation with all required fields")
    public void testCreateAuthorWithValidDataReturnsSuccess() {
        logStep("Generate test author data");
        Author newAuthor = TestDataGenerator.generateRandomAuthor();
        logStep("Create a new author: " + newAuthor.getFullName());
        Response response = authorService.createAuthor(newAuthor);
        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 OK")
                .isEqualTo(StatusCodes.OK);
        logStep("Verify response contains created author with ID");
        Author createdAuthor = authorService.extractAuthor(response);
        assertThat(createdAuthor.getId())
                .as("Created author should have an ID")
                .isNotNull();
        logStep("Verify created author data matches request");
        assertThat(createdAuthor.getFirstName())
                .as("First name should match the request")
                .isEqualTo(newAuthor.getFirstName());
        assertThat(createdAuthor.getLastName())
                .as("Last name should match the request")
                .isEqualTo(newAuthor.getLastName());
        log.info("Test passed: Author created with ID {}", createdAuthor.getId());
    }

    @Test(description = "Verify creating multiple authors with unique data",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that multiple authors can be created in sequence")
    public void testCreateMultipleAuthorsSuccessfully() {
        logStep("Generate and create 5 random authors");
        for (int i = 1; i <= 5; i++) {
            Author author = TestDataGenerator.generateRandomAuthor();
            logStep("Create author " + i + ": " + author.getFullName());
            Response response = authorService.createAuthor(author);
            assertThat(response.getStatusCode())
                    .as("Each author creation should return 200")
                    .isEqualTo(StatusCodes.OK);
            Author createdAuthor = authorService.extractAuthor(response);
            assertThat(createdAuthor.getId())
                    .as("Each created author should have an ID")
                    .isNotNull();
        }
        log.info("Test passed: Successfully created 5 authors");
    }


    @Test(description = "Verify creating an author with minimal required fields",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that author can be created with only required fields (id, idBook) and optional fields are handled correctly.")
    public void testCreateAuthorWithMinimalData() {
        logStep("Create author with minimal data (id and idBook must be provided)");
        Author minimalAuthor = Author.createMinimalAuthor();
        Response response = authorService.createAuthor(minimalAuthor);
        logStep("Verify response status code is 200/201");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 or 201 for minimal author")
                .isIn(StatusCodes.OK, StatusCodes.CREATED);
        Author createdAuthor = authorService.extractAuthor(response);
        logStep("Verifying required fields were assigned/preserved.");
        assertThat(createdAuthor.getId())
                .as("Created author must have a valid ID.")
                .isNotNull();
        assertThat(createdAuthor.getIdBook())
                .as("idBook must be preserved.")
                .isEqualTo(minimalAuthor.getIdBook());
        logStep("Verifying optional fields (firstName, lastName) are null or empty as expected.");
        assertThat(createdAuthor.getFirstName())
                .as("First name should be null or empty string if not required.")
                .isIn(null, "", "Unknown");
        assertThat(createdAuthor.getLastName())
                .as("Last name should be null or empty string if not required.")
                .isIn(null, "");
        log.info("Test passed: Minimal author created successfully. Optional fields were correctly handled.");
    }

    @Test(description = "Verify API behavior for each field when null (Author)",
            groups = {"regression", "negative", "validation"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests each field of the Author model with null value to identify required vs optional fields.")
    public void testCreateAuthorWithEachFieldNull() {
        String[] fieldsToTest = {"id", "idBook", "firstName", "lastName"};
        List<String> requiredFields = new ArrayList<>();
        List<String> optionalFields = new ArrayList<>();
        logStep("Testing author creation with each field set to null individually");
        for (String fieldName : fieldsToTest) {
            logStep("Test: Creating author with null " + fieldName);
            Author authorWithNullField = TestDataGenerator.generateAuthorWithNullField(fieldName);
            Response response = authorService.createAuthor(authorWithNullField);
            int statusCode = response.getStatusCode();
            if (statusCode == StatusCodes.BAD_REQUEST || statusCode == StatusCodes.UNPROCESSABLE_ENTITY) {
                requiredFields.add(fieldName);
                logStep("Field '"+ fieldName +"' is REQUIRED - API returns " + statusCode);
            } else if (statusCode == StatusCodes.OK || statusCode == StatusCodes.CREATED) {
                optionalFields.add(fieldName);
                log.info("Field '{}' is OPTIONAL - API accepts null (returns {})", fieldName, statusCode);
            } else {
                log.warn("Unexpected status code {} for null field '{}'", statusCode, fieldName);
            }
            assertThat(statusCode)
                    .as("API response when " + fieldName + " is null")
                    .isIn(StatusCodes.OK, StatusCodes.CREATED, StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY)
                    .withFailMessage("Unexpected status code for null " + fieldName + ": " + statusCode);
        }
        log.info("REQUIRED Fields (Gerekli): {}", requiredFields.isEmpty() ? "NONE" : String.join(", ", requiredFields));
        log.info("OPTIONAL Fields (İsteğe Bağlı): {}", optionalFields.isEmpty() ? "NONE" : String.join(", ", optionalFields));

    }

    @Test(description = "Verify creating an author with book ID",
            groups = {"regression", "happy-path"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates author creation with book association")
    public void testCreateAuthorWithBookId() {
        logStep("Create author with book ID");
        Author author = TestDataGenerator.generateRandomAuthor();
        author.setIdBook(1);
        Response response = authorService.createAuthor(author);
        assertThat(response.getStatusCode())
                .as("Status code should be 200")
                .isEqualTo(StatusCodes.OK);
        Author createdAuthor = authorService.extractAuthor(response);
        log.info("created author {}", createdAuthor);
        assertThat(createdAuthor.getIdBook())
                .as("Book ID should be preserved")
                .isEqualTo(author.getIdBook());
        assertThat(createdAuthor.getId())
                .as("Author ID should be preserved")
                .isEqualTo(author.getId());
        log.info("Test passed: Author ID created");
    }


    @Test(description = "Verify response time for author creation is acceptable",
            groups = {"regression", "performance"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that author creation response time is within limits")
    public void testCreateAuthorResponseTime() {
        logStep("Create author and measure response time");
        Author author = TestDataGenerator.generateRandomAuthor();
        Response response = authorService.createAuthor(author);
        logStep("Verify response time is less than 3 seconds");
        assertThat(response.getTime())
                .as("Response time should be acceptable")
                .isLessThan(3000L);
        log.info("Test passed: Response time {} ms", response.getTime());
    }
}