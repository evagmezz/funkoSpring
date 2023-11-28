package dev.rest.users.controllers;

import dev.rest.pedido.models.Pedido;
import dev.rest.pedido.services.PedidoService;
import dev.rest.users.dto.UserInfoResponse;
import dev.rest.users.dto.UserRequest;
import dev.rest.users.dto.UserResponse;
import dev.rest.users.exceptions.UserException;
import dev.rest.users.exceptions.UserNoAuthorized;
import dev.rest.users.exceptions.UserNotFound;
import dev.rest.users.models.User;
import dev.rest.users.services.UsersService;
import dev.utils.pagination.PageResponse;
import dev.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/users")
@PreAuthorize("hasRole('USER')")
public class UsersController {
    private final UsersService usersService;
    private final PedidoService pedidoService;
    private final PaginationLinksUtils paginationLinksUtils;

    public UsersController(UsersService usersService, PedidoService pedidoService, PaginationLinksUtils paginationLinksUtils) {
        this.usersService = usersService;
        this.pedidoService = pedidoService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("findAll: username: {}, email: {}, isDeleted: {}, page: {}, size: {}, sortBy: {}, direction: {}",
                username, email, isDeleted, page, size, sortBy, direction);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<UserResponse> pageResult = usersService.findAll(username, email, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        log.info("findById: id: {}", id);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        return ResponseEntity.ok(usersService.findById(id, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("save: userRequest: {}", userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.save(userRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        log.info("update: id: {}, userRequest: {}", id, userRequest);
        return ResponseEntity.ok(usersService.update(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("delete: id: {}", id);
        usersService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/porfile")
    public ResponseEntity<UserInfoResponse> actualUser(@AuthenticationPrincipal User user) {
        log.info("Obteniendo usuario...");
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        return ResponseEntity.ok(usersService.findById(user.getId(), pageable));
    }

    @PutMapping("/me/porfile")
    public ResponseEntity<UserResponse> updateActualUser(@AuthenticationPrincipal User user, @Valid @RequestBody UserRequest userRequest) {
        log.info("updateMe: user: {}, userRequest: {}", user, userRequest);
        return ResponseEntity.ok(usersService.update(user.getId(), userRequest));
    }

    @DeleteMapping("/me/porfile")
    public ResponseEntity<Void> deleteActualUser(@AuthenticationPrincipal User user) {
        log.info("deleteMe: user: {}", user);
        usersService.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/pedidos")
    public ResponseEntity<PageResponse<Pedido>> getPedidosByUsuario(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        log.info("Obteniendo pedidos del usuario con ID: " + user.getId());
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(pedidoService.findByIdUsuario(user.getId(), pageable), sortBy, direction));
    }

    @GetMapping("/me/pedidos/{id}")
    public ResponseEntity<Pedido> getPedidoById(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido
    ) {
        log.info("Obteniendo pedido con ID: " + idPedido);
        return ResponseEntity.ok(pedidoService.findById(idPedido));
    }

    @PostMapping("/me/pedidos")
    public ResponseEntity<Pedido> savePedido(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Pedido pedido
    ) {
        log.info("Creando pedido...");
        pedido.setIdUsuario(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.save(pedido));
    }

    @PutMapping("/me/pedidos/{id}")
    public ResponseEntity<Pedido> updatePedido(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido,
            @Valid @RequestBody Pedido pedido) {
        log.info("Actualizando pedido con ID: " + idPedido);
        pedido.setIdUsuario(user.getId());
        return ResponseEntity.ok(pedidoService.update(idPedido, pedido));
    }

    @DeleteMapping("/me/pedidos/{id}")
    public ResponseEntity<Void> deletePedidoById(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido
    ) {
        log.info("Borrando pedido con ID: " + idPedido);
        Pedido pedido = pedidoService.findById(idPedido);
        if (!pedido.getIdUsuario().equals(user.getId())) {
            throw new UserNoAuthorized("El usuario no es el propietario del pedido") {
            };
        }
        pedidoService.delete(idPedido);
        return ResponseEntity.noContent().build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
