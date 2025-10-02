package com.bookstore.constants;

/**
 * API endpoint constants.
 * Centralized location for all API endpoints.
 *
 * @author API Automation Team
 * @version 1.0
 */
public final class EndPoints {

    // Private constructor to prevent instantiation
    private EndPoints() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Books API Endpoints
    public static final String BOOKS = "/Books";
    public static final String BOOKS_BY_ID = "/Books/{id}";

    // Authors API Endpoints
    public static final String AUTHORS = "/Authors";
    public static final String AUTHORS_BY_ID = "/Authors/{id}";

    /**
     * Builds the books endpoint with specific ID.
     *
     * @param bookId the book identifier
     * @return formatted endpoint path
     */
    public static String getBooksById(int bookId) {
        return BOOKS + "/" + bookId;
    }

    /**
     * Builds the authors endpoint with specific ID.
     *
     * @param authorId the author identifier
     * @return formatted endpoint path
     */
    public static String getAuthorsById(int authorId) {
        return AUTHORS + "/" + authorId;
    }
}