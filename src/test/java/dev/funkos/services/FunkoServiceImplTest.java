package dev.funkos.services;

import dev.config.websocket.WebSocketConfig;
import dev.config.websocket.WebSocketHandler;
import dev.rest.categoria.models.Categoria;
import dev.rest.categoria.repositories.CategoriaRepository;
import dev.rest.funkos.dto.FunkoCreateDto;
import dev.rest.funkos.dto.FunkoResponseDto;
import dev.rest.funkos.dto.FunkoUpdateDto;
import dev.rest.funkos.exceptions.FunkoNotFound;
import dev.rest.funkos.mappers.FunkoMapper;
import dev.rest.funkos.models.Funko;
import dev.rest.funkos.repositories.FunkoRepository;
import dev.rest.funkos.services.FunkoServiceImpl;
import dev.rest.storage.services.StorageService;
import dev.websockets.notifications.mapper.FunkoNotificationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {

    private final Categoria categoria1 = Categoria.builder().id(1L).name("Categoria 1").build();
    private final Categoria categoria2 = Categoria.builder().id(2L).name("Categoria 2").build();
    private final Funko funko1 = Funko.builder()
            .id(1L)
            .nombre("Funko 1")
            .precio(100.0)
            .cantidad(10)
            .rutaImagen("ruta1")
            .categoria(categoria1)
            .build();

    private final Funko funko2 = Funko.builder()
            .id(2L)
            .nombre("Funko 2")
            .precio(200.0)
            .cantidad(20)
            .rutaImagen("ruta2")
            .categoria(categoria2)
            .build();

    private final FunkoResponseDto funkoResponseDto1 = new FunkoResponseDto(
            1L,
            "Funko 1",
            100.0,
            10,
            "ruta1",
            LocalDate.now(),
            LocalDate.now(),
            categoria1
    );

    private final FunkoResponseDto funkoResponseDto2 = new FunkoResponseDto(
            2L,
            "Funko 2",
            200.0,
            20,
            "ruta2",
            LocalDate.now(),
            LocalDate.now(),
            categoria2
    );

    WebSocketHandler webSocketHandler = mock(WebSocketHandler.class);
    @Mock
    private FunkoRepository funkoRepository;
    @Mock
    private FunkoMapper funkoMapper;
    @Mock
    private WebSocketConfig webSocketConfig;
    @Mock
    private FunkoNotificationMapper funkoNotificationMapper;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private StorageService storageService;

    @InjectMocks
    private FunkoServiceImpl funkoService;
    @Captor
    private ArgumentCaptor<Funko> funkoCaptor;


    @Test
    void findAll() {
        List<Funko> funkoEsperado = Arrays.asList(funko1, funko2);
        List<FunkoResponseDto> funkoResponseDtoEsperado = Arrays.asList(funkoResponseDto1, funkoResponseDto2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Funko> expectedPage = new PageImpl<>(funkoEsperado);

        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(funkoMapper.toFunkoDto(any(Funko.class))).thenReturn(funkoResponseDto1);

        Page<FunkoResponseDto> funkoActual = funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
        System.out.println(funkoActual.getTotalElements());

        assertAll("findAll",
                () -> assertNotNull(funkoActual),
                () -> assertFalse(funkoActual.isEmpty()),
                () -> assertTrue(funkoActual.getTotalElements() > 0)
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(funkoMapper, times(2)).toFunkoDto(any(Funko.class));
    }

    @Test
    void findAllByNombre() {
        Optional<String> nombre = Optional.of("Funko 1");
        List<Funko> funkoEsperado = List.of(funko1);
        List<FunkoResponseDto> funkoResponseEsperado = List.of(funkoResponseDto1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Funko> expectedPage = new PageImpl<>(funkoEsperado);
        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(funkoMapper.toFunkoDto(any(Funko.class))).thenReturn(funkoResponseDto1);

        Page<FunkoResponseDto> funkoActual = funkoService.findAll(nombre, Optional.empty(), Optional.empty(), pageable);
        assertAll(
                () -> assertEquals(funkoResponseEsperado, funkoActual.getContent()),
                () -> assertEquals(1, funkoActual.getTotalPages()),
                () -> assertEquals(1, funkoActual.getTotalElements()),
                () -> assertEquals(0, funkoActual.getNumber()),
                () -> assertEquals(1, funkoActual.getSize())
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(funkoMapper, times(1)).toFunkoDto(any(Funko.class));
    }

    @Test
    void findAllByCategoria() {
        Optional<String> nombreCategoria = Optional.of("Categoria 1");
        List<Funko> funkoEsperado = List.of(funko2);
        List<FunkoResponseDto> funkoResponseEsperado = List.of(funkoResponseDto2);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Funko> expectedPage = new PageImpl<>(funkoEsperado);
        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(funkoMapper.toFunkoDto(any(Funko.class))).thenReturn(funkoResponseDto1);

        Page<FunkoResponseDto> funkoActual = funkoService.findAll(Optional.empty(), nombreCategoria, Optional.empty(), pageable);
        assertAll(
                () -> assertNotNull(funkoActual),
                () -> assertFalse(funkoActual.isEmpty()),
                () -> assertTrue(funkoActual.getTotalElements() > 0)
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(funkoMapper, times(1)).toFunkoDto(any(Funko.class));
    }

    @Test
    void findAllByNombreAndCategoria() {
        Optional<String> nombre = Optional.of("Funko 1");
        Optional<String> nombreCategoria = Optional.of("Categoria 1");
        List<Funko> funkoEsperado = List.of(funko1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        Page<Funko> expectedPage = new PageImpl<>(funkoEsperado);

        when(funkoRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(expectedPage);
        when(funkoMapper.toFunkoDto(any(Funko.class))).thenReturn(funkoResponseDto1);

        Page<FunkoResponseDto> funkoActual = funkoService.findAll(nombre, nombreCategoria, Optional.empty(), pageable);

        assertAll(
                () -> assertEquals(1, funkoActual.getTotalPages()),
                () -> assertEquals(1, funkoActual.getTotalElements()),
                () -> assertEquals(0, funkoActual.getNumber()),
                () -> assertEquals(1, funkoActual.getSize())
        );

        verify(funkoRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
        verify(funkoMapper, times(1)).toFunkoDto(any(Funko.class));
    }

    @Test
    void findById() {
        Long id = 1L;
        FunkoResponseDto funkoEsperado = funkoResponseDto1;
        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko1));
        when(funkoMapper.toFunkoDto(any(Funko.class))).thenReturn(funkoEsperado);
        FunkoResponseDto funkoActual = funkoService.findById(id);
        assertEquals(funkoEsperado, funkoActual);
        verify(funkoRepository, times(1)).findById(id);
        verify(funkoMapper, times(1)).toFunkoDto(any(Funko.class));
    }

    @Test
    void findByIdNotFound() {
        Long id = 1L;
        when(funkoRepository.findById(id)).thenReturn(Optional.empty());
        var resultado = assertThrows(FunkoNotFound.class, () -> funkoService.findById(id));
        assertEquals("No hay ningun Funko con id " + id, resultado.getMessage());
        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void save() throws IOException {
        FunkoCreateDto funkoCreateDto = new FunkoCreateDto("Funko 1", 100.0, 10, "ruta1", categoria1.getName());

        Funko funkoEsperado = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(100.0)
                .cantidad(10)
                .rutaImagen("ruta1")
                .categoria(categoria1)
                .build();

        when(categoriaRepository.findByNameContainingIgnoreCase(funkoCreateDto.categoria())).thenReturn(Optional.of(categoria1));
        when(funkoRepository.save(any(Funko.class))).thenReturn(funkoEsperado);
        when(funkoMapper.toFunko(any(FunkoCreateDto.class), any(Categoria.class))).thenReturn(funkoEsperado);
        when(funkoMapper.toFunkoDto(funkoEsperado)).thenReturn(new FunkoResponseDto(
                funkoEsperado.getId(),
                funkoEsperado.getNombre(),
                funkoEsperado.getPrecio(),
                funkoEsperado.getCantidad(),
                funkoEsperado.getRutaImagen(),
                funkoEsperado.getFechaCreacion(),
                funkoEsperado.getFechaActualizacion(),
                funkoEsperado.getCategoria()
        ));
        doNothing().when(webSocketHandler).sendMessage(any());

        FunkoResponseDto funkoActual = funkoService.save(funkoCreateDto);

        assertAll(
                () -> assertEquals(funkoEsperado.getNombre(), funkoActual.nombre()),
                () -> assertEquals(funkoEsperado.getPrecio(), funkoActual.precio()),
                () -> assertEquals(funkoEsperado.getCantidad(), funkoActual.cantidad()),
                () -> assertEquals(funkoEsperado.getRutaImagen(), funkoActual.rutaImagen()),
                () -> assertEquals(funkoEsperado.getCategoria(), funkoActual.categoria())
        );

        verify(categoriaRepository, times(1)).findByNameContainingIgnoreCase(funkoCreateDto.categoria());
        verify(funkoRepository, times(1)).save(funkoCaptor.capture());
        verify(funkoMapper, times(1)).toFunko(funkoCreateDto, categoria1);

    }

    @Test
    void update() throws IOException {
        Long id = 1L;
        FunkoUpdateDto funkoUpdateDto = new FunkoUpdateDto("Funko 1", 100.0, 10, "ruta1", categoria1.getName());
        Funko funko = funko1;
        FunkoResponseDto funkoResponseDto = new FunkoResponseDto(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCantidad(),
                funko.getRutaImagen(),
                funko.getFechaCreacion(),
                funko.getFechaActualizacion(),
                funko.getCategoria()
        );

        when(funkoRepository.findById(id)).thenReturn(Optional.of(funko));
        when(categoriaRepository.findByNameContainingIgnoreCase(funkoUpdateDto.categoria())).thenReturn(Optional.of(categoria1));
        when(funkoRepository.save(funko)).thenReturn(funko);
        when(funkoMapper.toFunko(funkoUpdateDto, funko1, categoria1)).thenReturn(funko);
        when(funkoMapper.toFunkoDto(funko)).thenReturn(funkoResponseDto);
        doNothing().when(webSocketHandler).sendMessage(any());

        FunkoResponseDto funkoActual = funkoService.update(funkoUpdateDto, id);

        assertAll(
                () -> assertEquals(funkoResponseDto, funkoActual),
                () -> assertEquals(funkoResponseDto.nombre(), funkoActual.nombre()),
                () -> assertEquals(funkoResponseDto.precio(), funkoActual.precio()),
                () -> assertEquals(funkoResponseDto.cantidad(), funkoActual.cantidad()),
                () -> assertEquals(funkoResponseDto.rutaImagen(), funkoActual.rutaImagen()),
                () -> assertEquals(funkoResponseDto.categoria(), funkoActual.categoria())
        );

        verify(funkoRepository, times(1)).save(any(Funko.class));
        verify(funkoRepository, times(1)).findById(id);
        verify(funkoMapper, times(1)).toFunko(any(FunkoUpdateDto.class), any(Funko.class), any(Categoria.class));
        verify(funkoMapper, times(1)).toFunkoDto(any(Funko.class));
    }


    @Test
    void deleteById() throws IOException {
        Long id = 1L;
        Funko funkoEsperado = funko1;
        when(funkoRepository.findById(id)).thenReturn(Optional.of(funkoEsperado));
        doNothing().when(webSocketHandler).sendMessage(any());

        funkoService.deleteById(id);

        verify(funkoRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteByIdNotFound() {
        Long id = 1L;

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        var resultado = assertThrows(FunkoNotFound.class, () -> funkoService.findById(id));
        assertEquals("No hay ningun Funko con id " + id, resultado.getMessage());

        verify(funkoRepository, times(0)).deleteById(id);

    }

    @Test
    void updateImage() throws IOException {
        String imageUrl = "rutaImagen4";

        FunkoResponseDto funkoResponseDto = new FunkoResponseDto(
                1L,
                "Funko 1",
                100.0,
                10,
                imageUrl,
                null,
                null,
                categoria1
        );

        MultipartFile multipartFile = mock(MultipartFile.class);

        when(funkoRepository.findById(funko1.getId())).thenReturn(Optional.of(funko1));
        when(storageService.store(multipartFile)).thenReturn(imageUrl);
        when(funkoRepository.save(any(Funko.class))).thenReturn(funko1);
        when(funkoMapper.toFunkoDto(any(Funko.class))).thenReturn(funkoResponseDto);
        doNothing().when(webSocketHandler).sendMessage(anyString());

        FunkoResponseDto funkoActualizado = funkoService.updateImage(funko1.getId(), multipartFile, false);

        assertEquals(funkoActualizado.rutaImagen(), imageUrl);
        verify(funkoRepository, times(1)).save(any(Funko.class));
        verify(storageService, times(1)).delete(funko1.getRutaImagen());
        verify(storageService, times(1)).store(multipartFile);
        verify(funkoMapper, times(1)).toFunkoDto(any(Funko.class));
    }

}