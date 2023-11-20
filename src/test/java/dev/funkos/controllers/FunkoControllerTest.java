package dev.funkos.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.rest.categoria.models.Categoria;
import dev.rest.funkos.dto.FunkoCreateDto;
import dev.rest.funkos.dto.FunkoResponseDto;
import dev.rest.funkos.dto.FunkoUpdateDto;
import dev.rest.funkos.exceptions.FunkoNotFound;
import dev.rest.funkos.models.Funko;
import dev.rest.funkos.services.FunkoServiceImpl;
import dev.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class FunkoControllerTest {

    private final String myEndpoint = "/api/funkos";

    private final Categoria categoria1 = Categoria.builder()
            .id(1L)
            .name("Categoria 1")
            .build();

    private final Categoria categoria2 = Categoria.builder()
            .id(2L)
            .name("Categoria 2")
            .build();

    private final Funko funko1 = Funko.builder()
            .id(1L)
            .nombre("Funko 1")
            .precio(100.0)
            .cantidad(10)
            .rutaImagen("ruta1")
            .categoria(categoria1)
            .fechaCreacion(LocalDate.now())
            .fechaActualizacion(LocalDate.now())
            .build();

    private final Funko funko2 = Funko.builder()
            .id(2L)
            .nombre("Funko 2")
            .precio(200.0)
            .cantidad(20)
            .rutaImagen("ruta2")
            .categoria(categoria2)
            .fechaCreacion(LocalDate.now())
            .fechaActualizacion(LocalDate.now())
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


    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    FunkoServiceImpl funkoService;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    public FunkoControllerTest(FunkoServiceImpl funkoService) {
        this.funkoService = funkoService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getFunkoById() throws Exception {
        var localEndpoint = myEndpoint + "/2";
        when(funkoService.findById(2L)).thenReturn(funkoResponseDto2);

        MockHttpServletResponse response = mockMvc.perform(
                get(localEndpoint)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        FunkoResponseDto funkos = mapper.readValue(response.getContentAsString(), FunkoResponseDto.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko2.getId(), funkos.id()),
                () -> assertEquals(funko2.getNombre(), funkos.nombre()),
                () -> assertEquals(funko2.getPrecio(), funkos.precio()),
                () -> assertEquals(funko2.getCantidad(), funkos.cantidad()),
                () -> assertEquals(funko2.getRutaImagen(), funkos.rutaImagen()),
                () -> assertEquals(funko2.getCategoria(), funkos.categoria())
        );

    }

    @Test
    void getFunkos() throws Exception {
        var listaFunkos = List.of(funkoResponseDto1, funkoResponseDto2);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaFunkos);

        when(funkoService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(myEndpoint)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        PageResponse<FunkoResponseDto> funkos = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, funkos.content().size())
        );

        verify(funkoService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);

    }

    @Test
    void findFunkoByNombre() throws Exception {
        var funkoList = List.of(funkoResponseDto1);
        var localEndpoint = myEndpoint + "?nombre=Funko 1";
        Optional<String> nombre = Optional.of("Funko 1");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        when(funkoService.findAll(Optional.empty(), nombre, Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(localEndpoint)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        PageResponse<FunkoResponseDto> funkos = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, funkos.content().size())
        );

        verify(funkoService, times(1)).findAll(Optional.empty(), nombre, Optional.empty(), pageable);
    }

    @Test
    void findFunkoByCategoria() throws Exception {
        var funkoList = List.of(funkoResponseDto2);
        var localEndpoint = myEndpoint + "?categoria=Categoria 1";
        Optional<String> categoria = Optional.of("Categoria 1");
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(funkoList);

        when(funkoService.findAll(categoria, Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(localEndpoint)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        PageResponse<FunkoResponseDto> funkos = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, funkos.content().size())
        );

        verify(funkoService, times(1)).findAll(categoria, Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void findFunkoByNombreAndCategoria() throws Exception {
        var funkoList = List.of(funkoResponseDto1);
        Page<FunkoResponseDto> page = new PageImpl<>(funkoList);
        var localEndpoint = myEndpoint + "?nombre=Funko 1&categoria=Categoria 1";
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Optional<String> categoria = Optional.of("Categoria 1");
        Optional<String> nombre = Optional.of("Funko 1");

        when(funkoService.findAll(categoria, nombre, Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                get(localEndpoint)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        PageResponse<FunkoResponseDto> funkos = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, funkos.content().size())
        );

        verify(funkoService, times(1)).findAll(categoria, nombre, Optional.empty(), pageable);
    }

    @Test
    void findByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5";

        when(funkoService.findById(anyLong())).thenThrow(new FunkoNotFound(5L));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(funkoService, times(1)).findById(anyLong());
    }

    @Test
    void createFunko() throws Exception {
        var funkoDto = new FunkoCreateDto("Funko 1", 100.0, 10, "ruta1", categoria1.getName());
        when(funkoService.save(any(FunkoCreateDto.class))).thenReturn(funkoResponseDto1);

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(funkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        FunkoResponseDto funko = mapper.readValue(response.getContentAsString(), FunkoResponseDto.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(funko1.getId(), funko.id()),
                () -> assertEquals(funko1.getNombre(), funko.nombre()),
                () -> assertEquals(funko1.getPrecio(), funko.precio()),
                () -> assertEquals(funko1.getCantidad(), funko.cantidad()),
                () -> assertEquals(funko1.getRutaImagen(), funko.rutaImagen()),
                () -> assertEquals(funko1.getCategoria(), funko.categoria())
        );
    }

    @Test
    void createFunkoWithBadRequestNombre() throws Exception {
        var funkoDto = new FunkoCreateDto("", 100.0, 10, "ruta1", categoria1.getName());

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(funkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre no puede estar vacio"))
        );
    }

    @Test
    void createFunkoWithBadRequestPrecio() throws Exception {
        var funkoDto = new FunkoCreateDto("Funko 1", -100.0, 10, "ruta1", categoria1.getName());

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(funkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El precio debe ser mayor a 0"))
        );
    }

    @Test
    void createFunkoWithBadRequestCantidad() throws Exception {
        var funkoDto = new FunkoCreateDto("Funko 1", 100.0, -10, "ruta1", categoria1.getName());

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(funkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("La cantidad debe ser mayor a 0"))
        );
    }

    @Test
    void createFunkoWithBadRequestRutaImagen() throws Exception {
        var funkoDto = new FunkoCreateDto("Funko 1", 100.0, 10, "", categoria1.getName());

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(funkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("La ruta de la imagen no puede estar vacia"))
        );
    }

    @Test
    void createFunkoWithBadRequestCategoria() throws Exception {
        var funkoDto = new FunkoCreateDto("Funko 1", 100.0, 10, "ruta1", null);

        MockHttpServletResponse response = mockMvc.perform(
                post(myEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(funkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
    }

    @Test
    void updateFunko() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var FunkoDto = new FunkoUpdateDto("Funko 1", 100.0, 10, "ruta1", categoria1.getName());

        when(funkoService.update(any(FunkoUpdateDto.class), anyLong())).thenReturn(funkoResponseDto1);

        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(FunkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        FunkoResponseDto funko = mapper.readValue(response.getContentAsString(), FunkoResponseDto.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1.getId(), funko.id()),
                () -> assertEquals(funko1.getNombre(), funko.nombre()),
                () -> assertEquals(funko1.getPrecio(), funko.precio()),
                () -> assertEquals(funko1.getCantidad(), funko.cantidad()),
                () -> assertEquals(funko1.getRutaImagen(), funko.rutaImagen()),
                () -> assertEquals(funko1.getCategoria(), funko.categoria())
        );

        verify(funkoService, times(1)).update(any(FunkoUpdateDto.class), anyLong());
    }

    @Test
    void updateFunkoNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5";
        var FunkoDto = new FunkoUpdateDto("Funko 1", 100.0, 10, "ruta1", categoria1.getName());

        when(funkoService.update(any(FunkoUpdateDto.class), anyLong())).thenThrow(new FunkoNotFound(5L));

        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(FunkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(funkoService, times(1)).update(any(FunkoUpdateDto.class), anyLong());
    }

    @Test
    void updateFunkoWithBadRequestNombre() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var FunkoDto = new FunkoUpdateDto("", 100.0, 10, "ruta1", categoria1.getName());

        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(FunkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre no puede estar vacio"))
        );
    }

    @Test
    void updateFunkoWithBadRequestPrecio() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var FunkoDto = new FunkoUpdateDto("Funko 1", -100.0, 10, "ruta1", categoria1.getName());

        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(FunkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El precio debe ser mayor a 0"))
        );
    }

    @Test
    void updateFunkoWithBadRequestCantidad() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        FunkoUpdateDto FunkoDto = new FunkoUpdateDto("Funko 1", 100.0, -10, "ruta1", categoria1.getName());

        MockHttpServletResponse response = mockMvc.perform(
                put(myLocalEndpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(FunkoDto))
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("La cantidad debe ser mayor a 0"))
        );
    }

    @Test
    void updateFunkoPatch() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        var funkoDto = new FunkoUpdateDto("Funko 1", 100.0, 10, "ruta1", categoria1.getName());


        when(funkoService.update(funkoDto, 1L)).thenReturn(funkoResponseDto1);

        MockHttpServletResponse response = mockMvc.perform(
                        patch(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(funkoDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        FunkoResponseDto funko = mapper.readValue(response.getContentAsString(), FunkoResponseDto.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funko1.getId(), funko.id()),
                () -> assertEquals(funko1.getNombre(), funko.nombre()),
                () -> assertEquals(funko1.getPrecio(), funko.precio()),
                () -> assertEquals(funko1.getCantidad(), funko.cantidad()),
                () -> assertEquals(funko1.getRutaImagen(), funko.rutaImagen()),
                () -> assertEquals(funko1.getCategoria(), funko.categoria())
        );

        verify(funkoService, times(1)).update(any(FunkoUpdateDto.class), anyLong());
    }

    @Test
    void deleteFunko() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5";

        doNothing().when(funkoService).deleteById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(204, response.getStatus())
        );

        verify(funkoService, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteFunkoNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5";

        doThrow(new FunkoNotFound(5L)).when(funkoService).deleteById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                delete(myLocalEndpoint)
                        .accept(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(funkoService, times(1)).deleteById(anyLong());
    }

}