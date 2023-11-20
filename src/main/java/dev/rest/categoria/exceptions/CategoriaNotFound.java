package dev.rest.categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoriaNotFound extends CategoriaException {
    public CategoriaNotFound(String categoria) {
        super("Categoría " + categoria + " no encontrada");
    }

    public CategoriaNotFound(Long id) {
        super("Categoría con id " + id + " no encontrada");
    }
}
