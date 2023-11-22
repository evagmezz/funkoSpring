package dev.rest.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UsernameOrEmailExist extends UserException {
    public UsernameOrEmailExist(String message) {
        super(message);
    }
}
