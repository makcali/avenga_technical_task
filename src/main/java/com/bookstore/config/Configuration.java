package com.bookstore.config;

/**
 * Configuration interface defining all configuration properties.
 * Provides type-safe access to application settings.
 */
public interface Configuration {

    /**
     * Gets the base URL of the API
     * @return base URL as String
     */
    String getBaseUrl();

    /**
     * Gets the API version
     * @return API version (e.g., "v1")
     */
    String getApiVersion();

    /**
     * Gets the request timeout in seconds
     * @return timeout value
     */
    int getTimeout();

    /**
     * Gets the connection timeout in seconds
     * @return connection timeout value
     */
    int getConnectionTimeout();

    /**
     * Checks if logging is enabled
     * @return true if logging is enabled
     */
    boolean isLoggingEnabled();

    /**
     * Gets the log level
     * @return log level (DEBUG, INFO, WARN, ERROR)
     */
    String getLogLevel();

    /**
     * Checks if request/response logging is enabled
     * @return true if request logging is enabled
     */
    boolean isRequestLoggingEnabled();

    /**
     * Gets the retry count for failed tests
     * @return retry count
     */
    int getRetryCount();

    /**
     * Gets the environment name
     * @return environment (dev, staging, prod)
     */
    String getEnvironment();

    /**
     * Builds the complete API base path
     * @return complete base path with version (e.g., "https://api.example.com/api/v1")
     */
    default String getApiBasePath() {
        return getBaseUrl() + "/api/" + getApiVersion();
    }
}