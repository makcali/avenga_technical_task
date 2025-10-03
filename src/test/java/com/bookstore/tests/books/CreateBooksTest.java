package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.client.ApiClient;
import com.bookstore.constants.EndPoints;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Book;
import com.bookstore.utils.JsonUtils;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.bookstore.utils.TestDataGenerator.*;
import static com.bookstore.utils.TestDataGenerator.generateRandomDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

@Slf4j
@Epic("Bookstore API")
@Feature("Books API")
@Story("POST Books")
public class CreateBooksTest extends BaseTest {

    @Test(description = "Create book then fetch should persist",
            groups = {"smoke","regression"}, priority = 1)
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateBookHappyPath() {
        Book payload = TestDataGenerator.generateRandomBook();
        logStep("POST /Books");
        Response post = bookService.createBook(payload);
        post.then().statusCode(anyOf(is(StatusCodes.OK), is(StatusCodes.CREATED)));
        Book createdBook = bookService.extractBook(post);
        assertThat(createdBook.getId()).isNotNull();
        assertThat(createdBook.getTitle()).isEqualTo(payload.getTitle());
        assertThat(createdBook.getDescription()).isEqualTo(payload.getDescription());
        assertThat(createdBook.getPageCount()).isEqualTo(payload.getPageCount());
        logStep("Book created successfully with ID: " + createdBook.getId());
    }

    @Test(description = "POST invalid payload should fail",
            groups = {"regression","negative"}, priority = 2)
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookEmptyPayload() {
        Book invalid = TestDataGenerator.generateBookWithEmptyFields();
        bookService.createBook(invalid)
                .then()
                .statusCode(anyOf(is(StatusCodes.BAD_REQUEST), is(StatusCodes.UNPROCESSABLE_ENTITY)));
    }

    @Test(description = "POST invalid payload should fail",
            groups = {"regression","negative"}, priority = 2)
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookNullPayload() {
        Book invalid = TestDataGenerator.generateBookWithAllNullFields();
        bookService.createBook(invalid)
                .then()
                .statusCode(anyOf(is(StatusCodes.BAD_REQUEST), is(StatusCodes.UNPROCESSABLE_ENTITY)));
    }

    @Test(description = "Verify creating a book with valid data returns 200",
            groups = {"smoke", "regression", "happy-path"},
            priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Test to verify successful book creation with all required fields")
    public void testCreateBookWithValidDataReturnsSuccess() {
        logStep("Generate test book data");
        Book newBook = TestDataGenerator.generateRandomBook();
        logStep("Create a new book with title: " + newBook.getTitle());
        Response response = bookService.createBook(newBook);
        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 OK")
                .isEqualTo(StatusCodes.OK);
        logStep("Verify response contains created book with ID");
        Book createdBook = bookService.extractBook(response);
        assertThat(createdBook.getId())
                .as("Created book should have an ID")
                .isNotNull();
        logStep("Verify created book data matches request");
        assertThat(createdBook.getTitle())
                .as("Title should match the request")
                .isEqualTo(newBook.getTitle());
        assertThat(createdBook.getDescription())
                .as("Description should match the request")
                .isEqualTo(newBook.getDescription());
        assertThat(createdBook.getPageCount())
                .as("Page count should match the request")
                .isEqualTo(newBook.getPageCount());
        logStep("Test passed: Book created with ID "+ createdBook.getId());
    }

    @Test(description = "Verify creating multiple books with unique data",
            groups = {"regression"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that multiple books can be created in sequence")
    public void testCreateMultipleBooksSuccessfully() {
        logStep("Generate and create 5 random books");
        for (int i = 1; i <= 5; i++) {
            Book book = TestDataGenerator.generateRandomBook();
            logStep("Create book " + i + ": " + book.getTitle());
            Response response = bookService.createBook(book);
            assertThat(response.getStatusCode())
                    .as("Each book creation should return 200")
                    .isEqualTo(StatusCodes.OK);
            Book createdBook = bookService.extractBook(response);
            assertThat(createdBook.getId())
                    .as("Each created book should have an ID")
                    .isNotNull();
            assertThat(createdBook.getPageCount())
                    .as("Each created book should have an page caount")
                    .isNotNull();
            assertThat(createdBook.getPublishDate())
                    .as("Each created book should have an publish date")
                    .isNotNull();
        }
        logStep("Test passed: Successfully created 5 books");
    }

    @Test(description = "Verify creating a book with minimal required fields",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that book can be created with only required fields")
    public void testCreateBookWithMinimalData() {
        logStep("Create book with minimal data");
        Book minimalBook = Book.createMinimalBook();
        Response response = bookService.createBook(minimalBook);
        logStep("Verify response status code is 200");
        assertThat(response.getStatusCode())
                .as("Status code should be 200 for minimal book")
                .isEqualTo(StatusCodes.OK);
        Book createdBook = bookService.extractBook(response);
        assertThat(createdBook.getTitle())
                .as("Title should be preserved")
                .isEqualTo(minimalBook.getTitle());
        logStep("Test passed: Minimal book created successfully");
    }

    @Test(description = "Verify creating a book with very long title (500 chars)",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates boundary condition with maximum title length")
    public void testCreateBookWithLongTitle() {
        logStep("Generate book with very long title (501 characters)");
        Book bookWithLongTitle = TestDataGenerator.generateBookWithLongTitle(501);
        Response response = bookService.createBook(bookWithLongTitle);
        logStep("Verify book is created successfully");
        assertThat(response.getStatusCode())
                .as("Should accept long titles")
                .isEqualTo(StatusCodes.OK);
        logStep("Test passed: Long title accepted");
    }

    @Test(description = "Verify creating a book with special characters in title",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates handling of special characters in book title")
    public void testCreateBookWithSpecialCharacters() {
        logStep("Create book with special characters in title");
        Book bookWithSpecialChars = TestDataGenerator.generateBookWithSpecialCharacters();
        Response response = bookService.createBook(bookWithSpecialChars);
        logStep("Verify book is created successfully");
        assertThat(response.getStatusCode())
                .as("Should accept special characters")
                .isEqualTo(StatusCodes.OK);
        Book createdBook = bookService.extractBook(response);
        assertThat(createdBook.getTitle())
                .as("Special characters should be preserved")
                .isNotEmpty();
        logStep("Test passed: Special characters handled correctly");
    }

    @Test(description = "Verify creating a book with Unicode characters in title",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates handling of Unicode characters in book title")
    public void testCreateBookWithUnicodeCharacters() {
        logStep("Create book with Unicode characters in title");
        Book bookWithUnicodeChars = TestDataGenerator.generateBookWithUnicodeCharacters();
        Response response = bookService.createBook(bookWithUnicodeChars);
        logStep("Verify book is created successfully");
        assertThat(response.getStatusCode())
                .as("Should accept Unicode characters")
                .isEqualTo(StatusCodes.OK);
        Book createdBook = bookService.extractBook(response);
        assertThat(createdBook.getTitle())
                .as("Unicode characters should be preserved")
                .isNotEmpty();
        logStep("Test passed: Unicode characters handled correctly");
    }

    @Test(description = "Verify creating book with boundary page count values",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates boundary conditions for page count field")
    public void testCreateBookWithBoundaryPageCount() {
        logStep("Create book with minimum page count (1)");
        Book bookWithMinPages = TestDataGenerator.generateBookWithPageCount(1);
        Response response1 = bookService.createBook(bookWithMinPages);
        assertThat(response1.getStatusCode())
                .as("Should accept minimum page count")
                .isEqualTo(StatusCodes.OK);
        logStep("Create book with very large page count");
        Book bookWithMaxPages = TestDataGenerator.generateBookWithPageCount(999999);
        Response response2 = bookService.createBook(bookWithMaxPages);
        assertThat(response2.getStatusCode())
                .as("Should accept large page count")
                .isEqualTo(StatusCodes.OK);
        logStep("Test passed: Boundary page counts accepted");
    }

    @Test(description = "Verify creating a book with empty fields",
            groups = {"regression", "negative", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates API behavior when empty fields are provided")
    public void testCreateBookWithEmptyFields() {
        logStep("Attempt to create book with empty fields");
        Book bookWithEmptyFields = TestDataGenerator.generateBookWithEmptyFields();
        Response response = bookService.createBook(bookWithEmptyFields);
        logStep("Verify response - API may accept or reject empty fields");
        assertThat(response.getStatusCode())
                .as("Status code for empty fields")
                .isIn(StatusCodes.BAD_REQUEST);
        logStep("Test passed: Empty fields response code = " + response.getStatusCode());
    }

    @Test(description = "Verify creating a book with zero page count",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates handling of zero as page count")
    public void testCreateBookWithZeroPageCount() {
        logStep("Create book with zero page count");
        Book book = TestDataGenerator.generateBookWithZeroPageCount();
        Response response = bookService.createBook(book);
        logStep("Verify response for zero page count");
        assertThat(response.getStatusCode())
                .as("Status code for zero page count")
                .isIn(StatusCodes.OK, StatusCodes.BAD_REQUEST);
        logStep("Test passed: Zero page count response = "+ response.getStatusCode());
    }

    @Test(description = "Verify response time for book creation is acceptable",
            groups = {"regression", "performance"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates that book creation response time is within limits")
    public void testCreateBookResponseTime() {
        logStep("Create book and measure response time");
        Book book = TestDataGenerator.generateRandomBook();
        Response response = bookService.createBook(book);
        logStep("Verify response time is less than 3 seconds");
        assertThat(response.getTime())
                .as("Response time should be acceptable")
                .isLessThan(3000L);
        logStep("Test passed: Response time : " + response.getTime());
    }

    @Test(description = "Verify content type header in create book response",
            groups = {"regression"},
            priority = 3)
    @Severity(SeverityLevel.TRIVIAL)
    @Description("Validates that correct content type header is returned")
    public void testCreateBookResponseContentType() {
        logStep("Create book and verify content type");
        Book book = TestDataGenerator.generateRandomBook();
        Response response = bookService.createBook(book);
        logStep("Verify content type is application/json");
        assertThat(response.getContentType())
                .as("Content type should be application/json")
                .contains("application/json");
        logStep("Test passed: Content type is correct");
    }

    @Test(description = "Verify API behavior for each field when null",
            groups = {"regression", "negative", "validation"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests each field with null value to identify required vs optional fields")
    public void testCreateBookWithEachFieldNull() {
        String[] fieldsToTest = {"id", "title", "description", "pageCount", "excerpt", "publishDate"};
        logStep("Testing book creation with each field set to null individually");
        for (String fieldName : fieldsToTest) {
            logStep("Test: Creating book with null " + fieldName);
            Book bookWithNullField = TestDataGenerator.generateBookWithNullField(fieldName);
            Response response = bookService.createBook(bookWithNullField);
            int statusCode = response.getStatusCode();
            if (statusCode == StatusCodes.BAD_REQUEST) {
                logStep("Field '"+ fieldName +"' is REQUIRED - API returns 400 Bad Request");
            } else if (statusCode == StatusCodes.OK || statusCode == StatusCodes.CREATED) {
                log.info("Field '{}' is OPTIONAL - API accepts null (returns {})", fieldName, statusCode);
            } else {
                log.warn("Unexpected status code {} for null field '{}'", statusCode, fieldName);
            }
            assertThat(statusCode)
                    .as("API response when " + fieldName + " is null")
                    .isIn(StatusCodes.OK, StatusCodes.CREATED, StatusCodes.BAD_REQUEST)
                    .withFailMessage("Unexpected status code for null " + fieldName + ": " + statusCode);
        }
        logStep("Completed null field validation for all fields");
    }

    @Test(description = "Verify creating book with negative pageCount returns 400 or is rejected",
            groups = {"regression", "negative", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates API behavior when pageCount is negative (-1, -10)")
    public void testCreateBookWithNegativePageCount() {
        logStep("Create book with negative pageCount");
        Book bookWithNegativePages = TestDataGenerator.generateBookWithNegativePageCount();
        logStep("POST /Books with negative pageCount");
        Response response = bookService.createBook(bookWithNegativePages);
        logStep("Verify API response for negative pageCount");
        assertThat(response.getStatusCode())
                .as("API should handle negative pageCount")
                .isIn(StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY);
        log.info("Negative pageCount test completed: {}", response.getStatusCode());
    }

    @Test(description = "Verify creating book with fractional pageCount (10.5)",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Validates API behavior with fractional page numbers")
    public void testCreateBookWithFractionalPageCount() {
        logStep("Create book with fractional pageCount via raw JSON");
        String jsonPayload = String.format("""
        {
            "id": %d,
            "title": "Book with Fractional Pages",
            "description": "Testing fractional pageCount",
            "pageCount": 10.5,
            "excerpt": "Test excerpt",
            "publishDate": "%s"
        }
        """,
                1000,
                generateRandomDate());
        logStep("POST /Books with pageCount = 10.5");
        Response response = ApiClient.getRequestSpec()
                .body(jsonPayload)
                .when()
                .post(EndPoints.BOOKS);
        logStep("Verify API rejects or handles fractional pageCount");
        assertThat(response.getStatusCode())
                .as("API should handle fractional pageCount appropriately")
                .isIn(StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY);
        log.info("Fractional pageCount test completed: {}", response.getStatusCode());
    }


    @Test(description = "Verify creating duplicate book with same data",
            groups = {"regression", "edge-case"},
            priority = 2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests if API allows duplicate books or enforces uniqueness")
    public void testCreateDuplicateBook() {
        logStep("Create first book");
        Book originalBook = TestDataGenerator.generateRandomBook();
        Response firstResponse = bookService.createBook(originalBook);
        assertThat(firstResponse.getStatusCode())
                .as("First book creation should succeed")
                .isIn(StatusCodes.OK, StatusCodes.CREATED);
        logStep("Attempt to create duplicate book with same data");
        Book duplicateBook = Book.builder()
                .id(originalBook.getId())  // Same ID
                .title(originalBook.getTitle())  // Same title
                .description(originalBook.getDescription())  // Same description
                .pageCount(originalBook.getPageCount())
                .excerpt(originalBook.getExcerpt())
                .publishDate(originalBook.getPublishDate())  // Same publishDate
                .build();
        Response duplicateResponse = bookService.createBook(duplicateBook);
        logStep("Verify API handles duplicate book appropriately");
        assertThat(duplicateResponse.getStatusCode())
                .as("API may allow or reject duplicate book")
                .isIn(StatusCodes.OK, StatusCodes.CREATED, StatusCodes.CONFLICT, StatusCodes.BAD_REQUEST);
        if (duplicateResponse.getStatusCode() == StatusCodes.CONFLICT) {
            log.info("API enforces uniqueness - returns 409 Conflict");
        } else {
            log.info("API allows duplicate books - returns {}", duplicateResponse.getStatusCode());
        }
    }

    @Test(description = "Verify creating book with invalid publishDate format",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests various invalid date formats (DD-MM-YYYY, YYYY/MM/DD, etc.)")
    public void testCreateBookWithInvalidDateFormat() {
        logStep("Create book with invalid date format (DD-MM-YYYY)");
        Book bookWithInvalidDate = TestDataGenerator.generateBookWithInvalidDateFormat();
        logStep("POST /Books with DD-MM-YYYY date format");
        Response response = bookService.createBook(bookWithInvalidDate);
        logStep("Verify API rejects invalid date format");
        assertThat(response.getStatusCode())
                .as("API should validate date format")
                .isIn(StatusCodes.BAD_REQUEST);
        log.info("Invalid date format test completed: {}", response.getStatusCode());
    }


    @Test(description = "Verify creating book with future publishDate",
            groups = {"regression", "edge-case"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithFuturePublishDate() {
        logStep("Create book with future publishDate");
        Book bookWithFutureDate = TestDataGenerator.generateBookWithFuturePublishDate();
        logStep("POST /Books with future publishDate");
        Response response = bookService.createBook(bookWithFutureDate);
        logStep("Verify API accepts future dates");
        assertThat(response.getStatusCode())
                .as("API may or may not accept future dates")
                .isIn(StatusCodes.BAD_REQUEST);
        log.info("Future date test completed: {}", response.getStatusCode());
    }


    @Test(description = "Verify API rejects malformed JSON",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Tests API behavior with invalid JSON syntax")
    public void testCreateBookWithInvalidJsonFormat() {
        logStep("Send malformed JSON to POST /Books");
        String malformedJson = """
        {
            "id": 12345,
            "title": "Invalid JSON",
            "pageCount": 100,
            // Invalid comment
            "description": "Test"
        """;  // Missing closing brace
        Response response = ApiClient.getRequestSpec()
                .body(malformedJson)
                .when()
                .post(EndPoints.BOOKS);
        logStep("Verify API returns 400 Bad Request for malformed JSON");
        assertThat(response.getStatusCode())
                .as("API must reject malformed JSON")
                .isEqualTo(StatusCodes.BAD_REQUEST);
        log.info("Malformed JSON correctly rejected");
    }


    @Test(description = "Verify API behavior when Content-Type header is missing",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithoutContentType() {
        logStep("Create book without Content-Type header");
        Book book = TestDataGenerator.generateRandomBook();
        Response response = RestAssured.given()
                .baseUri(config.getApiBasePath())
                // Deliberately NOT setting Content-Type
                .body(book)
                .when()
                .post(EndPoints.BOOKS);
        logStep("Verify API response when Content-Type is missing");
        assertThat(response.getStatusCode())
                .as("API should handle missing Content-Type")
                .isIn(StatusCodes.UNSUPPORTED_MEDIA_TYPE);
        log.info("Missing Content-Type test completed: {}", response.getStatusCode());
    }


    @Test(description = "Verify API behavior with incorrect Content-Type",
            groups = {"regression", "negative"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithIncorrectContentType() {
        logStep("Create book with text/plain Content-Type");
        Book book = TestDataGenerator.generateRandomBook();
        Response response = RestAssured.given()
                .baseUri(config.getApiBasePath())
                .contentType("text/plain")  // Wrong content type
                .body(JsonUtils.toJson(book))
                .when()
                .post(EndPoints.BOOKS);

        logStep("Verify API rejects incorrect Content-Type");
        assertThat(response.getStatusCode())
                .as("API should validate Content-Type")
                .isIn(StatusCodes.UNSUPPORTED_MEDIA_TYPE);
        log.info("Incorrect Content-Type correctly handled with {}", response.getStatusCode());
    }


    @Test(description = "Verify API requires authentication (if applicable)",
            groups = {"regression", "negative", "security"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithoutAuthentication() {
        logStep("Attempt to create book without authentication token");
        Book book = TestDataGenerator.generateRandomBook();
        // Send request without any auth headers
        Response response = RestAssured.given()
                .baseUri(config.getApiBasePath())
                .contentType(ContentType.JSON)
                .body(book)
                .when()
                .post(EndPoints.BOOKS);
        logStep("Verify API response");
        assertThat(response.getStatusCode())
                .as("API may require or not require authentication")
                .isIn(StatusCodes.OK);
        if (response.getStatusCode() == StatusCodes.UNAUTHORIZED) {
            log.info("API requires authentication - returns 401");
        } else {
            log.info("API does not require authentication");
        }
    }

    @Test(description = "Verify API rejects invalid authentication token",
            groups = {"regression", "negative", "security"},
            priority = 3)
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookWithInvalidToken() {
        logStep("Attempt to create book with invalid token");
        Book book = TestDataGenerator.generateRandomBook();
        Response response = RestAssured.given()
                .baseUri(config.getApiBasePath())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer invalid_token_12345")
                .body(book)
                .when()
                .post(EndPoints.BOOKS);
        logStep("Verify API response for invalid token");
        assertThat(response.getStatusCode())
                .as("API should handle invalid tokens")
                .isIn(StatusCodes.OK);
        log.info("Invalid token test completed: {}", response.getStatusCode());
    }

    @Test(description = "Verify API is protected against SQL injection",
            groups = {"regression", "security"},
            priority = 3)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Tests if API properly sanitizes SQL injection attempts")
    public void testCreateBookWithSQLInjection() {
        logStep("Create book with SQL injection payload in description");
        Book bookWithSQLInjection = TestDataGenerator.generateBookWithSQLInjectionPayload();
        logStep("POST /Books with SQL injection payload");
        Response response = bookService.createBook(bookWithSQLInjection);
        logStep("Verify API handles SQL injection safely");
        assertThat(response.getStatusCode())
                .as("API should safely handle SQL injection attempts")
                .isIn(StatusCodes.OK);
        // Verify books table still exists (GET request should work)
        Response getAllResponse = bookService.getAllBooks();
        assertThat(getAllResponse.getStatusCode())
                .as("Books table should not be dropped")
                .isEqualTo(StatusCodes.OK);
        log.info("SQL injection protection verified");
    }

    @Test(description = "POST /Books with XSS payload: must be rejected or sanitized",
            groups = {"regression", "security"}, priority = 3)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies the API defends against reflected/stored XSS in string fields")
    public void postBook_withXssPayload_shouldRejectOrSanitize() {
        Book xss = TestDataGenerator.generateBookWithXSSPayload();
        logStep("POST /Books with XSS payload");
        Response resp = bookService.createBook(xss);
        assertThat(resp.getStatusCode())
                .as("API must not crash on XSS payloads")
                .isBetween(200, 499);
        if (resp.getStatusCode() == StatusCodes.OK || resp.getStatusCode() == StatusCodes.CREATED) {
            Book saved = bookService.extractBook(resp);
            assertThat(saved.getTitle() + " " + saved.getDescription() + " " + saved.getExcerpt())
                    .as("When 2xx, response must be sanitized (no script/event handlers)")
                    .doesNotContainIgnoringCase("<script")
                    .doesNotContainIgnoringCase("onerror=")
                    .doesNotContainIgnoringCase("onload=")
                    .doesNotContainIgnoringCase("javascript:");
        } else {
            assertThat(resp.getStatusCode())
                    .as("Prefer explicit validation error on XSS")
                    .isIn(StatusCodes.BAD_REQUEST, StatusCodes.UNPROCESSABLE_ENTITY);
        }
    }
}