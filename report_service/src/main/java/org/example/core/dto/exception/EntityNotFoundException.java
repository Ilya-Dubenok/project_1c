package org.example.core.dto.exception;

public class EntityNotFoundException extends RuntimeException {

    private static final String MESSAGE_FORMAT = "No %s found for this id";

    public EntityNotFoundException() {
        super("No report was found");
    }

    public EntityNotFoundException(String entityName) {
        super(String.format(MESSAGE_FORMAT, entityName));
    }
}
