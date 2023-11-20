package dev.rest.pedido.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PedidoNotFound extends PedidoException {
    public PedidoNotFound(String id) {
        super("Pedido con id " + id + " no encontrado");
    }
}
