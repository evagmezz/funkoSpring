package dev.rest.funkos.controllers;

import dev.rest.funkos.dto.FunkoCreateDto;
import dev.rest.funkos.dto.FunkoResponseDto;
import dev.rest.funkos.dto.FunkoUpdateDto;
import dev.rest.funkos.models.Funko;
import dev.rest.funkos.services.FunkoServiceImpl;
import dev.rest.storage.services.StorageService;
import dev.utils.pagination.PageResponse;
import dev.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/funkos")
@RestController
public class FunkoController {

    private final FunkoServiceImpl funkoService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public FunkoController(FunkoServiceImpl funkoService, StorageService storageService, PaginationLinksUtils paginationLinksUtils) {
        this.funkoService = funkoService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<FunkoResponseDto>> getFunkos(@RequestParam(required = false) Optional<String> categoria,
                                                                    @RequestParam(required = false) Optional<String> nombre,
                                                                    @RequestParam(required = false) Optional<Double> maxPrecio,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam(defaultValue = "id") String sortBy,
                                                                    @RequestParam(defaultValue = "asc") String direction,
                                                                    HttpServletRequest request) {
        log.info("Buscando funkos...");
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<FunkoResponseDto> result = funkoService.findAll(categoria, nombre, maxPrecio, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(result, uriBuilder))
                .body(PageResponse.of(result, sortBy, direction));
    }

    @GetMapping("/{id}")
    public FunkoResponseDto getFunkoById(@PathVariable Long id) {
        log.info("Buscando funko con ID: " + id);
        return funkoService.findById(id);
    }

    @NonNull
    @PostMapping()
    public ResponseEntity<FunkoResponseDto> createFunko(@Valid @RequestBody FunkoCreateDto funko) {
        log.info("Creando funko...");
        return ResponseEntity.status(HttpStatus.CREATED).body(funkoService.save(funko));
    }

    @NonNull
    @PutMapping("/{id}")
    public ResponseEntity<FunkoResponseDto> updateFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funko) {
        log.info("Actualizando funko con ID: " + id);
        return ResponseEntity.ok(funkoService.update(funko, id));
    }

    @NonNull
    @PatchMapping("/{id}")
    public ResponseEntity<FunkoResponseDto> funkoPatch(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funko) {
        log.info("Actualizando funko con ID: " + id);
        return ResponseEntity.ok(funkoService.update(funko, id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunko(@PathVariable Long id) {
        funkoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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

    @PatchMapping(value = "/image/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FunkoResponseDto> uploadImage(@PathVariable Long id, @RequestPart("file") MultipartFile
            file) {
        log.info("Actualizando imagen de funko con id: " + id);
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen debe tener extensión .jpg o .jpeg");
        }
        return ResponseEntity.ok(funkoService.updateImage(id, file, true));
    }
}