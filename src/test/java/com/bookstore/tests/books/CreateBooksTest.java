package com.bookstore.tests.books;

import com.bookstore.base.BaseTest;
import com.bookstore.constants.StatusCodes;
import com.bookstore.models.Book;
import com.bookstore.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

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
        int createdId = post.then()
                .statusCode(anyOf(is(StatusCodes.OK), is(StatusCodes.CREATED)))
                .extract().jsonPath().getInt("id");

        logStep("GET /Books/{id} to verify persist");
        Response get = bookService.getBookById(createdId);
        Book fetched = bookService.extractBook(get);
        assertThat(fetched.getTitle()).isEqualTo(payload.getTitle());
        assertThat(fetched.getPageCount()).isEqualTo(payload.getPageCount());
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
        Book invalid = TestDataGenerator.generateBookWithNullFields();
        bookService.createBook(invalid)
                .then()
                .statusCode(anyOf(is(StatusCodes.BAD_REQUEST), is(StatusCodes.UNPROCESSABLE_ENTITY)));
    }
}