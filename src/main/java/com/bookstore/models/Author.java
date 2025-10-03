package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data                           // Generates getters, setters, toString, equals, hashCode
@Builder(toBuilder = true)                      // Enables builder pattern: Book.builder().title("Test").build()
@NoArgsConstructor             // Generates no-args constructor
@AllArgsConstructor            // Generates all-args constructor
@JsonIgnoreProperties(ignoreUnknown = true)  // Ignores unknown JSON properties
public class Author {
    private static final Faker faker = new Faker();
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("idBook")
    private Integer idBook;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;



    public static Author createMinimalAuthor() {
        return Author.builder()
                .id(faker.number().randomDigit())
                .idBook(faker.number().randomDigit())
                .build();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }


    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty()
                && lastName != null && !lastName.trim().isEmpty();
    }
}