package org.example.core.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super("No product found for this uuid");
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
