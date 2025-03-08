package com.example.gym_bro_rest_api.controller;

public class BadNameException extends RuntimeException {
    public BadNameException() {
    }
    public BadNameException(String message) {
        super(message);
    }

    public BadNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadNameException(Throwable cause) {
        super(cause);
    }

    public BadNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
