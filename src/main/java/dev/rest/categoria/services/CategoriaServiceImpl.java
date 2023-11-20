package dev.rest.categoria.services;

import dev.rest.categoria.dto.CategoriaDto;
import dev.rest.categoria.exceptions.CategoriaConflict;
import dev.rest.categoria.exceptions.CategoriaNotFound;
import dev.rest.categoria.mappers.CategoriaMapper;
import dev.rest.categoria.models.Categoria;
import dev.rest.categoria.repositories.CategoriaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"categorias"})
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    @Override
    public Page<Categoria> findAll(Optional<String> name, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todas las categorias");
        Specification<Categoria> specNombre = (root, criteriaQuery, criteriaBuilder) ->
                name.map(value -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + value.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Categoria> specIsDeleted = (root, criteriaQuery, criteriaBuilder) ->
                isDeleted.map(value -> criteriaBuilder.equal(root.get("isDeleted"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Categoria> spec = Specification.where(specNombre).and(specIsDeleted);
        return categoriaRepository.findAll(spec, pageable);
    }


    @Override
    @Cacheable
    public Categoria findById(Long id) {
        log.info("Buscando categoria por id: {}", id);
        return categoriaRepository.findById(id).orElseThrow(() -> new CategoriaNotFound(id));
    }

    @Override
    @Cacheable
    public Categoria save(CategoriaDto categoriaDto) {
        log.info("Creando categoria: {}", categoriaDto);
        Optional<Long> id = categoriaRepository.getIdByName(categoriaDto.nombre());
        if (id.isPresent()) {
            throw new CategoriaConflict("Ya existe una categoría con el nombre " + categoriaDto.nombre());
        } else {
            Categoria categoria = categoriaMapper.toCategoria(categoriaDto);
            return categoriaRepository.save(categoria);
        }
    }

    @Override
    @Cacheable
    public Categoria update(Long id, CategoriaDto categoriaDto) {
        log.info("Actualizando categoría: " + categoriaDto);
        Categoria categoriaActual = findById(id);
        Categoria categoriaUpdated = categoriaMapper.toCategoria(categoriaDto, categoriaActual);
        return categoriaRepository.save(categoriaUpdated);
    }

    @Override
    @Cacheable
    @Transactional
    public void deleteById(Long id) {
        log.info("Borrando categoría con id: " + id);
        Categoria categoria = findById(id);
        categoriaRepository.updateIsActiveToFalseById(id);
        if (categoriaRepository.existsFunkoById(id)) {
            throw new CategoriaConflict("La categoría " + categoria.getName() + " tiene funkos asociados");
        } else {
            categoriaRepository.deleteById(id);
        }
    }
}
