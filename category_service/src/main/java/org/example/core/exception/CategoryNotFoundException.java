package org.example.core.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException() {
        super("No category found for this uuid");
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
