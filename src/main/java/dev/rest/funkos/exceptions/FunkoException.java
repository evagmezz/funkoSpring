package dev.rest.funkos.exceptions;

public abstract class FunkoException extends RuntimeException {
    public FunkoException(String message) {
        super(message);
    }
}
