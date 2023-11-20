package dev.websockets.notifications.dto;


import dev.rest.categoria.models.Categoria;

public record FunkoNotificationDto(
        Long id,

        String nombre,

        Double precio,

        Integer cantidad,

        String rutaImagen,

        String fechaCreacion,

        String fechaActualizacion,

        Categoria categoria
) {
}
