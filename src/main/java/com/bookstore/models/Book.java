package com.bookstore.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data                           // Generates getters, setters, toString, equals, hashCode
@Builder(toBuilder = true)                      // Enables builder pattern: Book.builder().title("Test").build()
@NoArgsConstructor             // Generates no-args constructor
@AllArgsConstructor            // Generates all-args constructor
@JsonIgnoreProperties(ignoreUnknown = true)  // Ignores unknown JSON properties
public class Book {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("pageCount")
    private Integer pageCount;

    @JsonProperty("excerpt")
    private String excerpt;

    @JsonProperty("publishDate")
    private String publishDate;


    public static Book createSampleBook() {
        return Book.builder()
                .title("Sample Book Title")
                .description("This is a sample book description for testing purposes")
                .pageCount(250)
                .excerpt("This is a sample excerpt from the book")
                .publishDate("2024-01-01T00:00:00")
                .build();
    }


    public static Book createMinimalBook() {
        return Book.builder()
                .title("Minimal Book")
                .pageCount(1)
                .build();
    }

}