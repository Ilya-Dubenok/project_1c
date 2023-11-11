package org.example.core.exception;

public class EntityNotFoundException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "No %s found for this uuid";

    public EntityNotFoundException() {
        super("Not found for this uuid");
    }

    public EntityNotFoundException(String entityName) {
        super(String.format(MESSAGE_FORMAT, entityName));
    }
}
