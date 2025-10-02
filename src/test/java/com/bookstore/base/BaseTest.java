package com.bookstore.base;

import com.bookstore.client.ApiClient;
import com.bookstore.config.ConfigurationManager;
import com.bookstore.services.AuthorService;
import com.bookstore.services.BookService;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

import java.lang.reflect.Method;

/**
 * Base test class providing common setup and teardown functionality.
 * All test classes should extend this class.
 * Implements Test Fixture pattern.
 *
 * @author API Automation Team
 * @version 1.0
 */
@Slf4j
@Listeners({com.bookstore.listeners.TestListener.class})
public abstract class BaseTest {

    protected BookService bookService;
    protected AuthorService authorService;
    protected ConfigurationManager config;

    /**
     * Suite level setup - runs once before all tests.
     * Initializes configuration and logs environment details.
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("=================================================");
        log.info("Starting Test Suite Execution");
        log.info("=================================================");

        config = ConfigurationManager.getInstance();
        config.logConfiguration();
    }

    /**
     * Method level setup - runs before each test method.
     * Initializes services and resets API client.
     *
     * @param method the test method about to be executed
     */
    @BeforeMethod(alwaysRun = true)
    @Step("Test Setup: {method.name}")
    public void setUp(Method method) {
        log.info("Starting test: {}.{}",
                method.getDeclaringClass().getSimpleName(),
                method.getName());

        // Initialize services
        bookService = new BookService();
        authorService = new AuthorService();

        // Reset API client to ensure clean state
        ApiClient.reset();
    }

    /**
     * Method level teardown - runs after each test method.
     * Logs test completion.
     *
     * @param method the test method that was executed
     */
    @AfterMethod(alwaysRun = true)
    @Step("Test Teardown: {method.name}")
    public void tearDown(Method method) {
        log.info("Completed test: {}.{}",
                method.getDeclaringClass().getSimpleName(),
                method.getName());
        log.info("-------------------------------------------------");
    }

    /**
     * Logs test step information for better reporting.
     *
     * @param stepDescription description of the step
     */
    @Step("{stepDescription}")
    protected void logStep(String stepDescription) {
        log.info("Test Step: {}", stepDescription);
    }

    /**
     * Gets a random valid book ID for testing.
     *
     * @return random book ID between 1 and 200
     */
    protected int getRandomBookId() {
        return (int) (Math.random() * 200) + 1;
    }

    /**
     * Gets a random valid author ID for testing.
     *
     * @return random author ID between 1 and 200
     */
    protected int getRandomAuthorId() {
        return (int) (Math.random() * 200) + 1;
    }

    /**
     * Gets an invalid book ID for negative testing.
     *
     * @return invalid book ID (negative number)
     */
    protected int getInvalidBookId() {
        return -1;
    }

    /**
     * Gets a non-existent book ID for testing.
     *
     * @return non-existent book ID
     */
    protected int getNonExistentBookId() {
        return 999999;
    }
}