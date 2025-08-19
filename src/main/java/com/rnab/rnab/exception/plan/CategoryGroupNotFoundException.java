package com.rnab.rnab.exception.plan;

public class CategoryGroupNotFoundException extends RuntimeException {
    public CategoryGroupNotFoundException(String message) {
        super(message);
    }

    public CategoryGroupNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
