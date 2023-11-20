package dev.rest.funkos.dto;


import dev.rest.categoria.models.Categoria;

import java.time.LocalDate;

public record FunkoResponseDto(
        Long id,

        String nombre,

        Double precio,

        Integer cantidad,

        String rutaImagen,

        LocalDate fechaCreacion,

        LocalDate fechaActualizacion,

        Categoria categoria
) {
}
