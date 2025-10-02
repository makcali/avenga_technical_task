package com.bookstore.client;

import com.bookstore.config.Configuration;
import com.bookstore.config.ConfigurationManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

/**
 * API Client for building REST Assured request specifications.
 * Implements Builder pattern for fluent API design.
 * Provides centralized configuration for all API calls.
 *
 * @author API Automation Team
 * @version 1.0
 */
@Slf4j
public class ApiClient {

    private static final Configuration config = ConfigurationManager.getInstance();
    private static RequestSpecification baseRequestSpec;

    // Static initialization block
    static {
        initializeBaseSpec();
    }

    /**
     * Initializes the base request specification with default settings.
     * Called once during class loading.
     */
    private static void initializeBaseSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(config.getApiBasePath())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("User-Agent", "API-Automation-Framework/1.0")
                .addFilter(new AllureRestAssured());

        // Add logging filters if enabled
        if (config.isRequestLoggingEnabled()) {
            builder.addFilter(new RequestLoggingFilter(LogDetail.ALL));
            builder.addFilter(new ResponseLoggingFilter(LogDetail.ALL));
        }

        baseRequestSpec = builder.build();

        // Configure timeouts
        RestAssured.config = RestAssured.config()
                .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", config.getConnectionTimeout() * 1000)
                        .setParam("http.socket.timeout", config.getTimeout() * 1000));

        log.info("ApiClient initialized with base URI: {}", config.getApiBasePath());
    }

    /**
     * Gets a new request specification based on the base spec.
     *
     * @return RequestSpecification instance
     */
    public static RequestSpecification getRequestSpec() {
        return RestAssured.given().spec(baseRequestSpec);
    }

    /**
     * Gets a request specification with custom content type.
     *
     * @param contentType desired content type
     * @return RequestSpecification instance
     */
    public static RequestSpecification getRequestSpec(ContentType contentType) {
        return RestAssured.given()
                .spec(baseRequestSpec)
                .contentType(contentType);
    }

    /**
     * Gets a request specification with custom headers.
     *
     * @param headers map of header key-value pairs
     * @return RequestSpecification instance
     */
    public static RequestSpecification getRequestSpecWithHeaders(java.util.Map<String, String> headers) {
        return RestAssured.given()
                .spec(baseRequestSpec)
                .headers(headers);
    }

    /**
     * Gets a request specification with authentication token.
     *
     * @param token authentication token
     * @return RequestSpecification instance
     */
    public static RequestSpecification getRequestSpecWithAuth(String token) {
        return RestAssured.given()
                .spec(baseRequestSpec)
                .auth().oauth2(token);
    }

    /**
     * Resets the API client configuration.
     * Useful for test cleanup or reconfiguration.
     */
    public static void reset() {
        RestAssured.reset();
        initializeBaseSpec();
        log.debug("ApiClient reset completed");
    }

    /**
     * Gets the base URL from configuration.
     *
     * @return base URL
     */
    public static String getBaseUrl() {
        return config.getApiBasePath();
    }
}