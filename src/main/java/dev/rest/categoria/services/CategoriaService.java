package dev.rest.categoria.services;

import dev.rest.categoria.dto.CategoriaDto;
import dev.rest.categoria.models.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoriaService {

    Page<Categoria> findAll(Optional<String> nombre, Optional<Boolean> isDeleted, Pageable pageable);

    Categoria findById(Long id);

    Categoria save(CategoriaDto categoriaDto);

    Categoria update(Long id, CategoriaDto categoriaDto);

    void deleteById(Long id);
}
