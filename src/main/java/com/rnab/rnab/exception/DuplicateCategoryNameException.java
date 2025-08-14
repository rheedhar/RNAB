package com.rnab.rnab.exception;

public class DuplicateCategoryNameException extends RuntimeException {
    public DuplicateCategoryNameException(String message) {
        super(message);
    }

    public DuplicateCategoryNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
