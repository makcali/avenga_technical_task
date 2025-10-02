package com.bookstore.utils;

import com.bookstore.models.Author;
import com.bookstore.models.Book;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility class for generating test data using JavaFaker.
 * Implements Factory pattern for test data creation.
 *
 * @author API Automation Team
 * @version 1.0
 */
@Slf4j
public class TestDataGenerator {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    // Private constructor to prevent instantiation
    private TestDataGenerator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Generates a random book with realistic data.
     *
     * @return Book with randomly generated data
     */
    public static Book generateRandomBook() {
        return Book.builder()
                .title(faker.book().title())
                .description(faker.lorem().sentence(15))
                .pageCount(faker.number().numberBetween(50, 1000))
                .excerpt(faker.lorem().paragraph())
                .publishDate(generateRandomDate())
                .build();
    }

    /**
     * Generates a random author with realistic data.
     *
     * @return Author with randomly generated data
     */
    public static Author generateRandomAuthor() {
        return Author.builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .idBook(faker.number().numberBetween(1, 100))
                .build();
    }

    /**
     * Generates multiple random books.
     *
     * @param count number of books to generate
     * @return List of Book objects
     */
    public static List<Book> generateRandomBooks(int count) {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            books.add(generateRandomBook());
        }
        log.debug("Generated {} random books", count);
        return books;
    }

    /**
     * Generates a book with specific page count.
     *
     * @param pageCount desired page count
     * @return Book with specified page count
     */
    public static Book generateBookWithPageCount(int pageCount) {
        Book book = generateRandomBook();
        book.setPageCount(pageCount);
        return book;
    }

    /**
     * Generates a book with very long title (for boundary testing).
     *
     * @return Book with long title
     */
    public static Book generateBookWithLongTitle() {
        Book book = generateRandomBook();
        book.setTitle(faker.lorem().characters(500));
        return book;
    }

    /**
     * Generates a book with empty fields (for negative testing).
     *
     * @return Book with empty fields
     */
    public static Book generateBookWithEmptyFields() {
        return Book.builder()
                .title("")
                .description("")
                .pageCount(0)
                .excerpt("")
                .publishDate("")
                .build();
    }

    /**
     * Generates a book with null fields (for negative testing).
     *
     * @return Book with null fields
     */
    public static Book generateBookWithNullFields() {
        return Book.builder()
                .title(null)
                .description(null)
                .pageCount(null)
                .excerpt(null)
                .publishDate(null)
                .build();
    }

    /**
     * Generates a book with invalid page count.
     *
     * @return Book with negative page count
     */
    public static Book generateBookWithInvalidPageCount() {
        Book book = generateRandomBook();
        book.setPageCount(-1 * faker.number().numberBetween(1, 100));
        return book;
    }

    /**
     * Generates a book with special characters in title.
     *
     * @return Book with special characters
     */
    public static Book generateBookWithSpecialCharacters() {
        Book book = generateRandomBook();
        book.setTitle("!@#$%^&*()_+{}|:<>?~`-=[]\\;',./");
        return book;
    }

    /**
     * Generates a random date string.
     *
     * @return formatted date string
     */
    private static String generateRandomDate() {
        LocalDateTime dateTime = LocalDateTime.now()
                .minusDays(random.nextInt(365 * 10));
        return dateTime.format(formatter);
    }

    /**
     * Generates a random positive integer.
     *
     * @param max maximum value
     * @return random integer between 1 and max
     */
    public static int generateRandomInt(int max) {
        return faker.number().numberBetween(1, max);
    }

    /**
     * Generates a random string of specified length.
     *
     * @param length desired length
     * @return random string
     */
    public static String generateRandomString(int length) {
        return faker.lorem().characters(length);
    }
}