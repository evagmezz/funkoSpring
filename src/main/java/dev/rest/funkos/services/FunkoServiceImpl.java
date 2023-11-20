package dev.rest.funkos.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.config.websocket.WebSocketConfig;
import dev.config.websocket.WebSocketHandler;
import dev.rest.categoria.models.Categoria;
import dev.rest.categoria.repositories.CategoriaRepository;
import dev.rest.funkos.dto.FunkoCreateDto;
import dev.rest.funkos.dto.FunkoResponseDto;
import dev.rest.funkos.dto.FunkoUpdateDto;
import dev.rest.funkos.exceptions.FunkoBadRequest;
import dev.rest.funkos.exceptions.FunkoNotFound;
import dev.rest.funkos.mappers.FunkoMapper;
import dev.rest.funkos.models.Funko;
import dev.rest.funkos.repositories.FunkoRepository;
import dev.rest.storage.services.StorageService;
import dev.websockets.notifications.dto.FunkoNotificationDto;
import dev.websockets.notifications.mapper.FunkoNotificationMapper;
import dev.websockets.notifications.models.Notification;
import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoServiceImpl implements FunkoService {
    private final FunkoRepository funkoRepository;
    private final FunkoMapper funkoMapper;
    private final StorageService storageService;
    private final WebSocketConfig webSocketConfig;
    private final FunkoNotificationMapper funkoNotificationMapper;
    private final ObjectMapper mapper;
    private WebSocketHandler webSocketService;
    private final CategoriaRepository categoryRepository;


    @Autowired
    public FunkoServiceImpl(FunkoRepository funkoRepository, FunkoMapper funkoMapper, StorageService storageService, WebSocketConfig webSocketConfig, FunkoNotificationMapper funkoNotificationMapper, ObjectMapper mapper, CategoriaRepository categoryRepository) {
        this.funkoRepository = funkoRepository;
        this.funkoMapper = funkoMapper;
        this.storageService = storageService;
        this.webSocketConfig = webSocketConfig;
        this.funkoNotificationMapper = funkoNotificationMapper;
        webSocketService = webSocketConfig.webSocketFunkosHandler();
        this.categoryRepository = categoryRepository;
        this.mapper = new ObjectMapper();
    }

    @Override
    public Page<FunkoResponseDto> findAll(Optional<String> categoria, Optional<String> nombre, Optional<Double> maxPrecio, Pageable pageable) {
        Specification<Funko> specCategoriaFunko = (root, query, criteriaBuilder) ->
                categoria.map(c -> {
                    Join<Funko, Categoria> categoriaJoin = root.join("categoria");
                    return criteriaBuilder.like(criteriaBuilder.lower(categoriaJoin.get("name")), "%" + c.toLowerCase() + "%");
                }).orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specNombreFunko = (root, query, criteriaBuilder) ->
                nombre.map(n -> criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + n.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specMinPrecioFunko = (root, query, criteriaBuilder) ->
                maxPrecio.map(p -> criteriaBuilder.lessThanOrEqualTo(root.get("precio"), p))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        Specification<Funko> criterio = Specification.where(specCategoriaFunko)
                .and(specCategoriaFunko)
                .and(specNombreFunko)
                .and(specMinPrecioFunko);
        return funkoRepository.findAll(criterio, pageable).map(funkoMapper::toFunkoDto);
    }


    @Override
    @Cacheable(key = "#id")
    public FunkoResponseDto findById(Long id) {
        return funkoMapper.toFunkoDto(funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound(id)));
    }

    private Categoria checkCategoria(String nameCategory) {
        var categoria = categoryRepository.findByNameContainingIgnoreCase(nameCategory);
        if (categoria.isEmpty() || categoria.get().getIsDeleted()) {
            throw new FunkoBadRequest("La categoria no existe");
        }
        return categoria.get();
    }

    @Override
    @CachePut(key = "#result.id")
    public FunkoResponseDto save(FunkoCreateDto funko) {
        var categoria = checkCategoria(funko.categoria());
        var funkoSaved = funkoRepository.save(funkoMapper.toFunko(funko, categoria));
        sendNotification(Notification.Tipo.CREATE, funkoSaved);
        return funkoMapper.toFunkoDto(funkoSaved);
    }

    @Override
    @Transactional
    @CachePut(key = "#id")
    public FunkoResponseDto update(FunkoUpdateDto funkoUpdateDto, Long id) {
        var funkoActual = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
        Categoria categoria = null;

        if (funkoUpdateDto.categoria() != null && !funkoUpdateDto.categoria().isEmpty()) {
            categoria = checkCategoria(funkoUpdateDto.categoria());
        } else {
            categoria = funkoActual.getCategoria();
        }

        var funkoUpdated = funkoRepository.save(funkoMapper.toFunko(funkoUpdateDto, funkoActual, categoria));
        sendNotification(Notification.Tipo.UPDATE, funkoUpdated);
        return funkoMapper.toFunkoDto(funkoUpdated);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        Funko funko = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
        funkoRepository.deleteById(id);
        if (funko.getRutaImagen() != null && !funko.getRutaImagen().equals(Funko.RUTA_IMAGEN)) {
            storageService.delete(funko.getRutaImagen());
        }
        sendNotification(Notification.Tipo.DELETE, funko);
    }

    @Override
    @CachePut(key = "#result.id")
    @Transactional
    public FunkoResponseDto updateImage(Long id, MultipartFile image, Boolean withUrl) {
        Funko funkoActual = funkoRepository.findById(id).orElseThrow(() -> new FunkoNotFound(id));
        if (funkoActual.getRutaImagen() != null && !funkoActual.getRutaImagen().equals(Funko.RUTA_IMAGEN)) {
            storageService.delete(funkoActual.getRutaImagen());
        }
        String imageStored = storageService.store(image);
        String imageUrl = !withUrl ? imageStored : storageService.getURL(imageStored);

        Funko funkoActualizado = Funko.builder()
                .id(funkoActual.getId())
                .nombre(funkoActual.getNombre())
                .precio(funkoActual.getPrecio())
                .cantidad(funkoActual.getCantidad())
                .rutaImagen(imageUrl)
                .fechaCreacion(funkoActual.getFechaCreacion())
                .fechaActualizacion(funkoActual.getFechaActualizacion())
                .categoria(funkoActual.getCategoria())
                .build();

        Funko funkoUpdate = funkoRepository.save(funkoActualizado);
        sendNotification(Notification.Tipo.UPDATE, funkoUpdate);
        return funkoMapper.toFunkoDto(funkoUpdate);
    }

    void sendNotification(Notification.Tipo tipo, Funko data) {
        if (webSocketService == null) {
            log.warn("No se ha configurado el servicio de websockets");
            webSocketService = this.webSocketConfig.webSocketFunkosHandler();
        }
        try {
            Notification<FunkoNotificationDto> notificacion = new Notification<>(
                    "FUNKOS",
                    tipo,
                    funkoNotificationMapper.toFunkoNotificationDto(data),
                    LocalDateTime.now().toString()
            );

            String json = mapper.writeValueAsString((notificacion));
            log.info("Enviando notificación mensaje..");

            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar el mensaje a través del servicio WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e) {
            log.error("Error al convertir la notificación a JSON", e);
        }
    }

    public void setWebSocketService(WebSocketHandler webSocketHandlerMock) {
        this.webSocketService = webSocketHandlerMock;
    }
}

