package dev.rest.pedido.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FunkoNotFound extends PedidoException {
    public FunkoNotFound(Long id) {
        super("Funko con id " + id + " no encontrado");
    }

    public FunkoNotFound(UUID uuid) {
        super("Funko con uuid " + uuid + " no encontrado");
    }
}
