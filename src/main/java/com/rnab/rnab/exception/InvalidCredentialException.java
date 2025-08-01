package com.rnab.rnab.exception;

public class InvalidCredentialException extends RuntimeException{

    public InvalidCredentialException(String message) {
        super(message);
    }

    public InvalidCredentialException(String message, Throwable cause) {
        super(message, cause);
    }
}
