package com.rnab.rnab.exception;

public class DuplicateCategoryGroupNameException extends RuntimeException {
    public DuplicateCategoryGroupNameException(String message) {
        super(message);
    }

    public DuplicateCategoryGroupNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
