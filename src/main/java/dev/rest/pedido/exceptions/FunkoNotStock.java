package dev.rest.pedido.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FunkoNotStock extends PedidoException {
    public FunkoNotStock(Long id) {
        super("Cantidad no válida o Funko con id " + id + " no tiene stock suficiente");
    }
}
