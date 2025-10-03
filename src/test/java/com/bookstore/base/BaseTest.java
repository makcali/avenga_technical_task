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

@Slf4j
@Listeners({com.bookstore.listeners.TestListener.class})
public abstract class BaseTest {

    protected BookService bookService;
    protected AuthorService authorService;
    protected ConfigurationManager config;


    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("=================================================");
        log.info("Starting Test Suite Execution");
        log.info("=================================================");

        config = ConfigurationManager.getInstance();
        config.logConfiguration();
    }


    @BeforeMethod(alwaysRun = true)
    @Step("Test Setup: {method.name}")
    public void setUp(Method method) {
        log.info("Starting test: {}.{}",
                method.getDeclaringClass().getSimpleName(),
                method.getName());
        bookService = new BookService();
        authorService = new AuthorService();
        ApiClient.reset();
    }


    @AfterMethod(alwaysRun = true)
    @Step("Test Teardown: {method.name}")
    public void tearDown(Method method) {
        log.info("Completed test: {}.{}",
                method.getDeclaringClass().getSimpleName(),
                method.getName());
        log.info("-------------------------------------------------");
    }


    @Step("{stepDescription}")
    protected void logStep(String stepDescription) {
        log.info("Test Step: {}", stepDescription);
    }

    protected int getRandomBookId() {
        return (int) (Math.random() * 200) + 1;
    }


    protected int getRandomAuthorId() {
        return (int) (Math.random() * 200) + 1;
    }


    protected int getInvalidBookId() {
        return -1;
    }


    protected int getNonExistentBookId() {
        return 999999;
    }
}