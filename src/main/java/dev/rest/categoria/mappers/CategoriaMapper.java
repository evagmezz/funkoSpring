package dev.rest.categoria.mappers;

import dev.rest.categoria.dto.CategoriaDto;
import dev.rest.categoria.models.Categoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriaMapper {
    public Categoria toCategoria(CategoriaDto request) {
        return new Categoria(
                null,
                request.nombre(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                true

        );
    }

    public Categoria toCategoria(CategoriaDto request, Categoria categoria) {
        return new Categoria(
                categoria.getId(),
                request.nombre() != null ? request.nombre() : categoria.getName(),
                categoria.getCreatedAt(),
                LocalDateTime.now(),
                request.isActive() != null ? request.isActive() : categoria.getIsDeleted()
        );
    }
}
