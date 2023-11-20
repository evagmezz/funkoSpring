package dev.rest.categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoriaConflict extends CategoriaException {

    public CategoriaConflict(String message) {
        super(message);
    }
}