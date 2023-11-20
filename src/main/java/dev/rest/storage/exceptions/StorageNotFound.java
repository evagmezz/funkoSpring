package dev.rest.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StorageNotFound extends StorageException {

    @Serial
    private static final long serialVersionUID = 211L;

    public StorageNotFound(String mensaje) {
        super(mensaje);
    }
}
