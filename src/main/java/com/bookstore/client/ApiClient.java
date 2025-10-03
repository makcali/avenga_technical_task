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


@Slf4j
public class ApiClient {

    private static final Configuration config = ConfigurationManager.getInstance();
    private static RequestSpecification baseRequestSpec;

    static {
        initializeBaseSpec();
    }


    private static void initializeBaseSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(config.getApiBasePath())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("User-Agent", "API-Automation-Framework/1.0")
                .addFilter(new AllureRestAssured());

        if (config.isRequestLoggingEnabled()) {
            builder.addFilter(new RequestLoggingFilter(LogDetail.ALL));
            builder.addFilter(new ResponseLoggingFilter(LogDetail.ALL));
        }
        baseRequestSpec = builder.build();

        RestAssured.config = RestAssured.config()
                .httpClient(io.restassured.config.HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", config.getConnectionTimeout() * 1000)
                        .setParam("http.socket.timeout", config.getTimeout() * 1000));
        log.info("ApiClient initialized with base URI: {}", config.getApiBasePath());
    }


    public static RequestSpecification getRequestSpec() {
        return RestAssured.given().spec(baseRequestSpec);
    }


    public static void reset() {
        RestAssured.reset();
        initializeBaseSpec();
        log.debug("ApiClient reset completed");
    }

    public static String getBaseUrl() {
        return config.getApiBasePath();
    }
}