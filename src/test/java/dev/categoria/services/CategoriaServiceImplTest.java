package dev.categoria.services;

import dev.rest.categoria.dto.CategoriaDto;
import dev.rest.categoria.exceptions.CategoriaNotFound;
import dev.rest.categoria.mappers.CategoriaMapper;
import dev.rest.categoria.models.Categoria;
import dev.rest.categoria.repositories.CategoriaRepository;
import dev.rest.categoria.services.CategoriaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {


    private final Categoria categoria = Categoria.builder().name("DISNEY").build();
    private final Categoria categoria2 = Categoria.builder().name("SUPERHEROES").build();

    private final CategoriaDto categoriaDto = new CategoriaDto("DISNEY", true);
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private CategoriaMapper categoriaMapper;
    @InjectMocks
    private CategoriaServiceImpl categoriaService;
    @Captor
    private ArgumentCaptor<Categoria> categoryCaptor;

    CategoriaServiceImplTest() {
    }

    @Test
    void findAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(List.of(categoria));

        when(categoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        var res = categoriaService.findAll(Optional.empty(), Optional.empty(), pageable);

        assertAll(
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty())
        );


        verify(categoriaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findByNombre() {
        Optional<String> name = Optional.of("Disney");
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> expectedPage = new PageImpl<>(List.of(categoria, categoria2));

        when(categoriaRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);

        var res = categoriaService.findAll(name, Optional.empty(), pageable);

        assertAll(
                () -> assertNotNull(res),
                () -> assertFalse(res.isEmpty())
        );

        verify(categoriaRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void findById() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        Categoria categoriaActual = categoriaService.findById(1L);
        assertAll(
                () -> assertEquals(categoria.getName(), categoriaActual.getName()),
                () -> assertEquals(categoria.getIsDeleted(), categoriaActual.getIsDeleted()),
                () -> assertEquals(categoria.getCreatedAt(), categoriaActual.getCreatedAt()),
                () -> assertEquals(categoria.getUpdatedAt(), categoriaActual.getUpdatedAt())
        );
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void findByIdNotFound() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
        var resultado = assertThrows(CategoriaNotFound.class, () -> categoriaService.findById(1L));
        assertAll(
                () -> assertEquals("Categoría con id 1 no encontrada", resultado.getMessage())
        );
        verify(categoriaRepository).findById(1L);
    }

    @Test
    void save() {
        when(categoriaMapper.toCategoria(categoriaDto)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        Categoria categoriaActual = categoriaService.save(categoriaDto);
        assertAll(
                () -> assertEquals(categoria.getName(), categoriaActual.getName()),
                () -> assertEquals(categoria.getIsDeleted(), categoriaActual.getIsDeleted()),
                () -> assertEquals(categoria.getCreatedAt(), categoriaActual.getCreatedAt()),
                () -> assertEquals(categoria.getUpdatedAt(), categoriaActual.getUpdatedAt())
        );
        verify(categoriaRepository).save(categoryCaptor.capture());
    }

    @Test
    void saveCategoriaExistente() {
        when(categoriaRepository.getIdByName("DISNEY")).thenReturn(Optional.of(1L));
        var resultado = assertThrows(RuntimeException.class, () -> categoriaService.save(categoriaDto));
        assertEquals("Ya existe una categoría con el nombre DISNEY", resultado.getMessage());
        verify(categoriaRepository).getIdByName("DISNEY");
    }

    @Test
    void update() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toCategoria(categoriaDto, categoria)).thenReturn(categoria);
        when(categoriaRepository.save(categoria)).thenReturn(categoria);
        Categoria categoriaActual = categoriaService.update(1L, categoriaDto);
        assertAll(
                () -> assertEquals(categoria.getName(), categoriaActual.getName()),
                () -> assertEquals(categoria.getIsDeleted(), categoriaActual.getIsDeleted()),
                () -> assertEquals(categoria.getCreatedAt(), categoriaActual.getCreatedAt()),
                () -> assertEquals(categoria.getUpdatedAt(), categoriaActual.getUpdatedAt())
        );
        verify(categoriaRepository).save(categoryCaptor.capture());
    }

    @Test
    void deleteById() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        categoriaService.deleteById(1L);
        verify(categoriaRepository).deleteById(1L);
    }

    @Test
    void deleteByIdWithFunko() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsFunkoById(1L)).thenReturn(true);
        var resultado = assertThrows(RuntimeException.class, () -> categoriaService.deleteById(1L));
        assertEquals("La categoría DISNEY tiene funkos asociados", resultado.getMessage());
        verify(categoriaRepository).findById(1L);
        verify(categoriaRepository).existsFunkoById(1L);
    }
}