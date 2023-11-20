package dev.rest.pedido.models;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LineaPedido {
    @Min(value = 1, message = "La cantidad del funko no puede ser negativa")
    @Builder.Default
    private Integer cantidad = 1;

    private Long idFunko;

    @Min(value = 0, message = "El precio del funko no puede ser negativo")
    @Builder.Default
    private Double precioFunko = 0.0;

    @Builder.Default
    private Double total = 0.0;

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        this.total = this.cantidad * this.precioFunko;
    }

    public void setPrecioFunko(Double precioFunko) {
        this.precioFunko = precioFunko;
        this.total = this.cantidad * this.precioFunko;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}