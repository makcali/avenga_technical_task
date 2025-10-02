package com.bookstore.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton Configuration Manager.
 * Manages application configuration using properties file.
 * Thread-safe implementation using double-checked locking.
 *
 * @author API Automation Team
 * @version 1.0
 */
@Slf4j
public class ConfigurationManager implements Configuration {

    private static volatile ConfigurationManager instance;
    private final Properties properties;

    /**
     * Private constructor to prevent instantiation.
     * Loads configuration from config.properties file.
     */
    private ConfigurationManager() {
        properties = new Properties();
        loadProperties();
        log.info("Configuration initialized for environment: {}", getEnvironment());
    }

    /**
     * Gets the singleton instance using double-checked locking pattern.
     * Thread-safe lazy initialization.
     *
     * @return ConfigurationManager instance
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    /**
     * Loads properties from config.properties file.
     * Throws RuntimeException if file is not found.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties file not found in resources!");
            }

            properties.load(input);
            log.debug("Properties loaded successfully");

        } catch (IOException e) {
            log.error("Failed to load configuration", e);
            throw new RuntimeException("Failed to load configuration: " + e.getMessage());
        }
    }

    @Override
    public String getBaseUrl() {
        return getProperty("base.url", "https://fakerestapi.azurewebsites.net");
    }

    @Override
    public String getApiVersion() {
        return getProperty("api.version", "v1");
    }

    @Override
    public int getTimeout() {
        return getIntProperty("timeout", 30);
    }

    @Override
    public int getConnectionTimeout() {
        return getIntProperty("connection.timeout", 10);
    }

    @Override
    public boolean isLoggingEnabled() {
        return getBooleanProperty("logging.enabled", true);
    }

    @Override
    public String getLogLevel() {
        return getProperty("log.level", "INFO");
    }

    @Override
    public boolean isRequestLoggingEnabled() {
        return getBooleanProperty("log.requests", true);
    }

    @Override
    public int getRetryCount() {
        return getIntProperty("retry.count", 2);
    }

    @Override
    public String getEnvironment() {
        return getProperty("environment", "dev");
    }

    /**
     * Gets a string property with default value.
     *
     * @param key property key
     * @param defaultValue default value if property not found
     * @return property value
     */
    private String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets an integer property with default value.
     *
     * @param key property key
     * @param defaultValue default value if property not found
     * @return property value as integer
     */
    private int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid integer value for key '{}': {}. Using default: {}",
                    key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Gets a boolean property with default value.
     *
     * @param key property key
     * @param defaultValue default value if property not found
     * @return property value as boolean
     */
    private boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }

    /**
     * Logs current configuration settings.
     * Useful for debugging and verification.
     */
    public void logConfiguration() {
        log.info("=== Configuration Settings ===");
        log.info("Environment: {}", getEnvironment());
        log.info("Base URL: {}", getBaseUrl());
        log.info("API Version: {}", getApiVersion());
        log.info("API Base Path: {}", getApiBasePath());
        log.info("Timeout: {} seconds", getTimeout());
        log.info("Connection Timeout: {} seconds", getConnectionTimeout());
        log.info("Logging Enabled: {}", isLoggingEnabled());
        log.info("Request Logging: {}", isRequestLoggingEnabled());
        log.info("Retry Count: {}", getRetryCount());
        log.info("==============================");
    }
}