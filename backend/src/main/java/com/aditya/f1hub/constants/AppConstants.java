package com.aditya.f1hub.constants;

public final class AppConstants {

    private AppConstants() {
        // Prevent instantiation
    }

    // ==========================
    // Success Messages
    // ==========================
    public static final String SUCCESS = "Success";
    public static final String CREATED = "Resource created successfully";
    public static final String UPDATED = "Resource updated successfully";
    public static final String DELETED = "Resource deleted successfully";
    public static final String FETCHED = "Data fetched successfully";

    // ==========================
    // Error Messages
    // ==========================
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String BAD_REQUEST = "Invalid request";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred";

}