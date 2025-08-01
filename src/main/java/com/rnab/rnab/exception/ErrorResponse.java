package com.rnab.rnab.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String errorCode;
    private String message;
    private int status;
    private LocalDateTime timeStamp;

    public ErrorResponse(String errorCode, String message, int status, LocalDateTime timeStamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.timeStamp = timeStamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
