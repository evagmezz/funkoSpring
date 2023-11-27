package dev.rest.categoria.controllers;


import dev.rest.categoria.dto.CategoriaDto;
import dev.rest.categoria.models.Categoria;
import dev.rest.categoria.services.CategoriaService;
import dev.utils.pagination.PageResponse;
import dev.utils.pagination.PaginationLinksUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
@Slf4j
@PreAuthorize("hasRole('USER')")
public class CategoriaController {
    private final CategoriaService categoriaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public CategoriaController(CategoriaService categoriasService, PaginationLinksUtils paginationLinksUtils) {
        this.categoriaService = categoriasService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @Operation(summary = "Obtener todas las categorías paginadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categorías encontradas correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @Parameters({
            @Parameter(name = "name", description = "Nombre de la categoría"),
            @Parameter(name = "isDeleted", description = "Indica si la categoría está eliminada"),
            @Parameter(name = "page", description = "Número de página"),
            @Parameter(name = "size", description = "Tamaño de la página"),
            @Parameter(name = "sortBy", description = "Campo por el que se ordenará"),
            @Parameter(name = "direction", description = "Dirección de la ordenación")
    })
    @GetMapping
    public ResponseEntity<PageResponse<Categoria>> getAll(@RequestParam(required = false) Optional<String> name,
                                                          @RequestParam(required = false) Optional<Boolean> isDeleted,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(defaultValue = "id") String sortBy,
                                                          @RequestParam(defaultValue = "asc") String direction,
                                                          HttpServletRequest request) {

        log.info("Buscando categorias...");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Categoria> categoriaPage = categoriaService.findAll(name, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(categoriaPage, uriBuilder))
                .body(PageResponse.of(categoriaPage, sortBy, direction));

    }

    @Operation(summary = "Obtener una categoría por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
    })
    @Parameters({
            @Parameter(name = "id", description = "ID de la categoría")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @Operation(summary = "Crear una nueva categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoría creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta")
    })
    @Parameters({
            @Parameter(name = "categoria", description = "Categoría a crear")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> create(@Valid @RequestBody CategoriaDto categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.save(categoria));
    }

    @Operation(summary = "Actualizar una categoría por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @Parameters({
            @Parameter(name = "id", description = "ID de la categoría"),
            @Parameter(name = "categoria", description = "Categoría a actualizar")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categoria> update(@PathVariable Long id, @Valid @RequestBody CategoriaDto categoria) {
        return ResponseEntity.ok(categoriaService.update(id, categoria));
    }

    @Operation(summary = "Eliminar una categoría por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @Parameters({
            @Parameter(name = "id", description = "ID de la categoría")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}