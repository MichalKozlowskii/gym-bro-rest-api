package com.example.gym_bro_rest_api.controller.exceptions;

public class InvalidExerciseException extends RuntimeException {
    public InvalidExerciseException() {
    }

    public InvalidExerciseException(String message) {
        super(message);
    }

    public InvalidExerciseException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidExerciseException(Throwable cause) {
        super(cause);
    }

    public InvalidExerciseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
