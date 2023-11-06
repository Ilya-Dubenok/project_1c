package org.example.core.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super("No product was found");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
