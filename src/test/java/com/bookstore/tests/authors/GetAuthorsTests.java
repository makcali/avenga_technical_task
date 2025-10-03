package com.bookstore.tests.authors;

import com.bookstore.base.BaseTest;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Author;
import io.qameta.allure.*;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;


@Slf4j
@Epic("Bookstore API")
@Feature("Authors API")
@Story("GET Authors")
public class GetAuthorsTests extends BaseTest {

    @Test(description = "Verify get all authors returns 200 and non-empty list",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to verify that GET /Authors returns successful response with authors list")
    public void testGetAllAuthorsReturnsSuccess() {
        logStep("Send GET request to retrieve all authors");
        Response response = authorService.getAllAuthors();
        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 OK")
                .isEqualTo(StatusCodes.OK);
        logStep("Verify response contains authors list");
        List<Author> authors = authorService.extractAuthors(response);
        assertThat(authors)
                .as("Authors list should not be null or empty")
                .isNotNull()
                .isNotEmpty();
        logStep("Verify first author has required fields");
        Author firstAuthor = authors.get(0);
        assertThat(firstAuthor.getId())
                .as("Author ID should not be null")
                .isNotNull();
        assertThat(firstAuthor.getFirstName())
                .as("Author first name should not be null")
                .isNotNull();
        log.info("Test passed: Found {} authors", authors.size());
    }

    @Test(description = "Verify get all authors response structure",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates the JSON response structure and data types")
    public void testGetAllAuthorsResponseStructure() {
        logStep("Send GET request and validate response structure");
        authorService.getAllAuthors()
                .then()
                .assertThat()
                .statusCode(StatusCodes.OK)
                .contentType("application/json")
                .body("$", hasSize(greaterThan(0)))
                .body("[0].id", notNullValue())
                .body("[0].firstName", notNullValue())
                .body("[0].lastName", notNullValue());
        log.info("Test passed: Response structure is valid");
    }


    @Test(description = "Verify get author by valid ID returns correct author",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates retrieval of a specific author by ID")
    public void testGetAuthorByIdReturnsCorrectAuthor() {
        int authorId = 1;
        logStep("Send GET request for author ID: " + authorId);
        Response response = authorService.getAuthorById(authorId);
        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 OK")
                .isEqualTo(StatusCodes.OK);
        logStep("Verify returned author has correct ID");
        Author author = authorService.extractAuthor(response);
        assertThat(author.getId())
                .as("Returned author should have ID: " + authorId)
                .isEqualTo(authorId);
        logStep("Verify author has all required fields populated");
        assertThat(author.getFirstName()).as("First name should not be null").isNotNull();
        assertThat(author.getLastName()).as("Last name should not be null").isNotNull();
        log.info("Test passed: Retrieved author '{}' with ID {}",
                author.getFullName(), authorId);
    }


    @Test(description = "Verify get author with non-existent ID returns 404",
            groups = {"regression", "edge-case", "negative"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates proper error handling for non-existent author IDs")
    public void testGetAuthorByNonExistentIdReturns404() {
        int nonExistentId = getNonExistentBookId();
        logStep("Send GET request for non-existent author ID: " + nonExistentId);
        Response response = authorService.getAuthorById(nonExistentId);
        logStep("Verify response status code is 404");
        assertThat(response.getStatusCode())
                .as("Status code should be 404 Not Found")
                .isEqualTo(StatusCodes.NOT_FOUND);
        log.info("Test passed: Got 404 for non-existent ID {}", nonExistentId);
    }


    @Test(description = "Verify get author with invalid ID returns 404",
            groups = {"regression", "edge-case", "negative"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates error handling for invalid negative author IDs")
    public void testGetAuthorByInvalidIdReturns404() {
        int invalidId = getInvalidBookId();
        logStep("Send GET request for invalid author ID: " + invalidId);
        Response response = authorService.getAuthorById(invalidId);
        logStep("Verify response status code is 404");
        assertThat(response.getStatusCode())
                .as("Status code should be 404 for invalid ID")
                .isEqualTo(StatusCodes.NOT_FOUND);
        log.info("Test passed: Got 404 for invalid ID {}", invalidId);
    }

    @Test(description = "Verify response time for get all authors is acceptable",
            groups = {"regression", "performance"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that API response time is within acceptable limits")
    public void testGetAllAuthorsResponseTime() {
        logStep("Send GET request and measure response time");
        Response response = authorService.getAllAuthors();
        logStep("Verify response time is less than 3 seconds");
        long responseTime = response.getTime();
        assertThat(responseTime)
                .as("Response time should be less than 3000ms")
                .isLessThan(3000L);
        log.info("Test passed: Response time {} ms", responseTime);
    }


    @Test(description = "Verify get all authors returns consistent data structure",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that all authors in the response have consistent structure")
    public void testGetAllAuthorsDataConsistency() {
        logStep("Send GET request to retrieve all authors");
        List<Author> authors = authorService.extractAuthors(authorService.getAllAuthors());
        logStep("Verify all authors have consistent structure");
        authors.forEach(author -> {
            assertThat(author.getId())
                    .as("Each author should have an ID")
                    .isNotNull();
            assertThat(author.getFirstName())
                    .as("Each author should have a first name")
                    .isNotNull();
            assertThat(author.getLastName())
                    .as("Each author should have a last name")
                    .isNotNull();
        });
        log.info(" Test passed: All {} authors have consistent structure", authors.size());
    }

    @Test(description = "Verify content type header is application/json",
            groups = {"regression"},
            priority = 3)
    @Severity(SeverityLevel.MINOR)
    @Description("Validates that the API returns correct content type header")
    public void testGetAllAuthorsContentType() {
        logStep("Send GET request and verify content type");
        Response response = authorService.getAllAuthors();
        logStep("Verify content type is application/json");
        assertThat(response.getContentType())
                .as("Content type should be application/json")
                .contains("application/json");
        log.info("Test passed: Content type is correct");
    }
}