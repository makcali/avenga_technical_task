package com.bookstore.constants;

/**
 * HTTP status code constants.
 * Provides meaningful names for HTTP response codes.
 *
 * @author API Automation Team
 * @version 1.0
 */
public final class StatusCodes {

    // Private constructor to prevent instantiation
    private StatusCodes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Success Codes (2xx)
    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int ACCEPTED = 202;
    public static final int NO_CONTENT = 204;

    // Client Error Codes (4xx)
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;
    public static final int UNPROCESSABLE_ENTITY = 422;

    // Server Error Codes (5xx)
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int GATEWAY_TIMEOUT = 504;
}