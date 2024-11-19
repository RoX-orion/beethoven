package org.beethoven.lib.exception;

public class MediaException extends RuntimeException {

    private final String message;

    public MediaException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
