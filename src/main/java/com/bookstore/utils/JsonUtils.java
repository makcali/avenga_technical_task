package com.bookstore.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for JSON operations using Jackson.
 * Provides serialization and deserialization capabilities.
 *
 * @author API Automation Team
 * @version 1.0
 */
@Slf4j
public final class JsonUtils {

    /**
     * -- GETTER --
     *  Gets the shared ObjectMapper instance.
     *
     * @return ObjectMapper instance
     */
    @Getter
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Static initialization block
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Private constructor to prevent instantiation
    private JsonUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts object to JSON string.
     *
     * @param object object to convert
     * @return JSON string or null if error occurs
     */
    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            log.error("Error converting object to JSON", e);
            return null;
        }
    }

    /**
     * Converts object to pretty JSON string.
     *
     * @param object object to convert
     * @return formatted JSON string or null if error occurs
     */
    public static String toPrettyJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (IOException e) {
            log.error("Error converting object to pretty JSON", e);
            return null;
        }
    }

    /**
     * Converts JSON string to object.
     *
     * @param json JSON string
     * @param clazz target class type
     * @param <T> generic type
     * @return deserialized object or null if error occurs
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("Error converting JSON to object", e);
            return null;
        }
    }

    /**
     * Reads JSON from file.
     *
     * @param file JSON file
     * @param clazz target class type
     * @param <T> generic type
     * @return deserialized object or null if error occurs
     */
    public static <T> T fromJsonFile(File file, Class<T> clazz) {
        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            log.error("Error reading JSON from file: {}", file.getPath(), e);
            return null;
        }
    }

    /**
     * Writes object to JSON file.
     *
     * @param object object to write
     * @param file target file
     */
    public static void toJsonFile(Object object, File file) {
        try {
            objectMapper.writeValue(file, object);
            log.debug("Successfully wrote JSON to file: {}", file.getPath());
        } catch (IOException e) {
            log.error("Error writing JSON to file: {}", file.getPath(), e);
        }
    }

}