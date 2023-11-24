package dev.rest.funkos.dto;


import dev.rest.categoria.models.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record FunkoResponseDto(
        @Schema(description = "Identificador del funko", example = "1")
        Long id,
        @Schema(description = "Nombre del funko", example = "Batman")
        String nombre,
        @Schema(description = "Precio del funko", example = "100.0")
        Double precio,
        @Schema(description = "Cantidad del funko", example = "10")
        Integer cantidad,
        @Schema(description = "Ruta de la imagen del funko", example = "imagen.jpg")
        String rutaImagen,
        @Schema(description = "Fecha de creación del funko", example = "2021-01-01")
        LocalDate fechaCreacion,
        @Schema(description = "Fecha de actualización del funko", example = "2021-01-01")
        LocalDate fechaActualizacion,
        @Schema(description = "Categoría del funko", example = "DC")
        Categoria categoria
) {
}
