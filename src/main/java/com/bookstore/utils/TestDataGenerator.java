package com.bookstore.utils;

import com.bookstore.models.Author;
import com.bookstore.models.Book;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Slf4j
public final class TestDataGenerator {

    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private static final Set<Integer> usedIds = new HashSet<>();

    private TestDataGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }


    public static int generateUniqueBookId() {
        int id;
        do {
            id = faker.number().numberBetween(1, 100000);
        } while (!usedIds.add(id));
        return id;
    }

    private static int generateUniqueAuthorId() {
        int id;
        do {
            id = faker.number().numberBetween(1000, 100000);
        } while (!usedIds.add(id));
        return id;
    }


    public static String generateRandomDate() {
        LocalDateTime dateTime = LocalDateTime.now()
                .minusDays(random.nextInt(365 * 10));
        return dateTime.format(formatter);
    }

    public static String generateFutureDate(int yearsInFuture) {
        return LocalDateTime.now()
                .plusYears(yearsInFuture)
                .format(formatter);
    }


    public static Book generateRandomBook() {
        return Book.builder()
                .id(generateUniqueBookId())
                .title(faker.book().title())
                .description(faker.lorem().sentence(15))
                .pageCount(faker.number().numberBetween(50, 1000))
                .excerpt(faker.lorem().paragraph())
                .publishDate(generateRandomDate())
                .build();
    }


    public static Book generateBookWithAllNullFields() {
        return Book.builder().build(); // All fields default to null
    }
    public static Book generateBookWithNullField(String fieldName) {
        Book validBook = generateRandomBook();
        return switch (fieldName.toLowerCase()) {
            case "id" -> validBook.toBuilder().id(null).build();
            case "title" -> validBook.toBuilder().title(null).build();
            case "description" -> validBook.toBuilder().description(null).build();
            case "pagecount" -> validBook.toBuilder().pageCount(null).build();
            case "excerpt" -> validBook.toBuilder().excerpt(null).build();
            case "publishdate" -> validBook.toBuilder().publishDate(null).build();
            default -> {
                log.warn("Unknown field name: {}", fieldName);
                yield validBook;
            }
        };
    }


    public static Book generateBookWithEmptyFields() {
        return Book.builder()
                .id(generateUniqueBookId())
                .title("")
                .description("")
                .pageCount(0)
                .excerpt("")
                .publishDate("")
                .build();
    }


    public static Book generateBookWithPageCount(int pageCount) {
        return generateRandomBook().toBuilder()
                .pageCount(pageCount)
                .build();
    }

    public static Book generateBookWithNegativePageCount() {
        return generateRandomBook().toBuilder()
                .pageCount(-1 * faker.number().numberBetween(1, 100))
                .build();
    }

    public static Book generateBookWithZeroPageCount() {
        return generateBookWithPageCount(0);
    }


    public static Book generateBookWithLongTitle(int length) {
        return generateRandomBook().toBuilder()
                .title(faker.lorem().characters(length))
                .build();
    }

    public static Book generateBookWithSpecialCharacters() {
        return generateRandomBook().toBuilder()
                .title("!@#$%^&*()_+{}|:<>?~`-=[]\\;',./")
                .build();
    }

    public static Book generateBookWithUnicodeCharacters() {
        return generateRandomBook().toBuilder()
                .title("Türkçe Kitap öçşğüıÖÇŞĞÜİ 测试书籍")
                .build();
    }

    public static Book generateBookWithFuturePublishDate() {
        return generateRandomBook().toBuilder()
                .publishDate(generateFutureDate(10))
                .build();
    }

    public static Book generateBookWithInvalidDateFormat() {
        return generateRandomBook().toBuilder()
                .publishDate("31-12-2023") // DD-MM-YYYY format
                .build();
    }

    public static Book generateBookWithSQLInjectionPayload() {
        return generateRandomBook().toBuilder()
                .description("'; DROP TABLE Books; --")
                .build();
    }

    public static Book generateBookWithXSSPayload() {
        return generateRandomBook().toBuilder()
                .title("<script>alert('XSS')</script>")
                .build();
    }


    public static Author generateRandomAuthor() {
        return Author.builder()
                .id(faker.number().numberBetween(1000, 100000))
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .idBook(faker.number().numberBetween(1000, 100000))
                .build();
    }


    public static Author generateAuthorWithNullField(String fieldName) {
        Author validAuthor = generateRandomAuthor();

        return switch (fieldName.toLowerCase()) {
            case "id" -> validAuthor.toBuilder().id(null).build();
            case "firstname" -> validAuthor.toBuilder().firstName(null).build();
            case "lastname" -> validAuthor.toBuilder().lastName(null).build();
            case "idbook" -> validAuthor.toBuilder().idBook(null).build();
            default -> {
                log.warn("Unknown author field: {}", fieldName);
                yield validAuthor;
            }
        };
    }
}