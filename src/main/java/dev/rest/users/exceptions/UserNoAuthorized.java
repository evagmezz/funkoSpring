package dev.rest.users.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserNoAuthorized extends UserException{
    public UserNoAuthorized(String message) {
        super(message);
    }
}
