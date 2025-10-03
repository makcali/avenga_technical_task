package com.bookstore.config;

import org.aeonbits.owner.Config;


public interface Configuration {

    String getBaseUrl();


    String getApiVersion();


    int getTimeout();

    int getConnectionTimeout();


    boolean isLoggingEnabled();


    String getLogLevel();

    boolean isRequestLoggingEnabled();


    int getRetryCount();

    String getEnvironment();


    default String getApiBasePath() {
        return getBaseUrl() + "/api/" + getApiVersion();
    }

    @Config.Key("deletion.persistence")
    @Config.DefaultValue("false")
    boolean deletionPersistence();
}