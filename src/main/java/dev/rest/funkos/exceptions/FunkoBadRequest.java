package dev.rest.funkos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FunkoBadRequest extends FunkoException {
    public FunkoBadRequest(String message) {
        super(message);
    }
}
