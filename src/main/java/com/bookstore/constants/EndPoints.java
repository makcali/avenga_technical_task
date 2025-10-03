package com.bookstore.constants;

public final class EndPoints {

    private EndPoints() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Books API Endpoints
    public static final String BOOKS = "/Books";
    public static final String BOOKS_BY_ID = "/Books/{id}";

    // Authors API Endpoints
    public static final String AUTHORS = "/Authors";
    public static final String AUTHORS_BY_ID = "/Authors/{id}";

}