package dev.rest.funkos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FunkoNotFound extends FunkoException {
    public FunkoNotFound(Long id) {
        super("No hay ningun Funko con id " + id);
    }
}
