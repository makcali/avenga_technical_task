package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author model representing the Author entity in the API.
 *
 * @author API Automation Team
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("idBook")
    private Integer idBook;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    /**
     * Creates a sample author with valid test data.
     *
     * @return Author instance with test data
     */
    public static Author createSampleAuthor() {
        return Author.builder()
                .firstName("John")
                .lastName("Doe")
                .idBook(1)
                .build();
    }

    /**
     * Creates a minimal valid author.
     *
     * @return Author instance with minimal data
     */
    public static Author createMinimalAuthor() {
        return Author.builder()
                .firstName("Test")
                .lastName("Author")
                .build();
    }

    /**
     * Gets the full name of the author.
     *
     * @return full name as "FirstName LastName"
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Validates if the author has all required fields.
     *
     * @return true if author is valid, false otherwise
     */
    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty()
                && lastName != null && !lastName.trim().isEmpty();
    }
}