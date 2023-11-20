package dev.rest.storage.exceptions;

import java.io.Serial;

public abstract class StorageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 211L;

    public StorageException(String message) {
        super(message);
    }
}
