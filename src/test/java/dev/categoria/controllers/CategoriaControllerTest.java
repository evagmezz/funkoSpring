package dev.categoria.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.rest.categoria.dto.CategoriaDto;
import dev.rest.categoria.exceptions.CategoriaNotFound;
import dev.rest.categoria.models.Categoria;
import dev.rest.categoria.services.CategoriaService;
import dev.utils.pagination.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
class CategoriaControllerTest {
    private final Categoria categoria = Categoria.builder().name("Categoria 1").build();
    private final Categoria categoria2 = Categoria.builder().name("Categoria 2").build();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private CategoriaService categoriaService;
    @Autowired
    private JacksonTester<Categoria> json;

    @Autowired
    public CategoriaControllerTest(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        String localEndPoint = "/api/categorias";
        var list = List.of(categoria, categoria2);
        Page<Categoria> page = new PageImpl<>(list);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());


        when(categoriaService.findAll(Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.get(localEndPoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(2, res.content().size())
        );

        verify(categoriaService, times(1)).findAll(Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void getAllByNombre() throws Exception {
        String localEndpoint = "/api/categorias?name=Categoria 1";
        var categoriaList = List.of(categoria);

        Optional<String> name = Optional.of("Categoria 1");
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Categoria> page = new PageImpl<>(categoriaList);

        when(categoriaService.findAll(name, Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.get(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        PageResponse<Categoria> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(categoriaService, times(1)).findAll(name, Optional.empty(), pageable);
    }

    @Test
    void getById() throws Exception {
        String localEndpoint = "/api/categorias/1";
        when(categoriaService.findById(1L)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.get(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Categoria actualCategory = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("Categoria 1", actualCategory.getName()),
                () -> assertEquals(categoria.getName(), actualCategory.getName())
        );
        verify(categoriaService, times(1)).findById(1L);
    }

    @Test
    void getByIdNotFound() throws Exception {
        String localEndpoint = "/api/categorias/1";
        when(categoriaService.findById(1L)).thenThrow(new CategoriaNotFound("Categoría con id 1 no encontrada"));

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.get(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(categoriaService, times(1)).findById(1L);
    }

    @Test
    void create() throws Exception {
        String localEndpoint = "/api/categorias";
        CategoriaDto categoriaDto = new CategoriaDto("Categoria 1", false);
        when(categoriaService.save(categoriaDto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.post(localEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Categoria actualCategory = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals("Categoria 1", actualCategory.getName()),
                () -> assertEquals(categoria.getName(), actualCategory.getName())
        );
        verify(categoriaService, times(1)).save(categoriaDto);
    }

    @Test
    void createBadRequest() throws Exception {
        String localEndpoint = "/api/categorias";
        CategoriaDto categoriaDto = new CategoriaDto("", false);
        when(categoriaService.save(categoriaDto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.post(localEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(categoriaService, times(0)).save(categoriaDto);

    }

    @Test
    void update() throws Exception {
        String localEndpoint = "/api/categorias/1";
        CategoriaDto categoriaDto = new CategoriaDto("Categoria 1", false);
        when(categoriaService.update(1L, categoriaDto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.put(localEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Categoria actualCategory = mapper.readValue(response.getContentAsString(), Categoria.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals("Categoria 1", actualCategory.getName()),
                () -> assertEquals(categoria.getName(), actualCategory.getName())
        );
        verify(categoriaService, times(1)).update(1L, categoriaDto);
    }

    @Test
    void updateBadRequest() throws Exception {
        String localEndpoint = "/api/categorias/1";
        CategoriaDto categoriaDto = new CategoriaDto("", false);
        when(categoriaService.update(1L, categoriaDto)).thenReturn(categoria);

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.put(localEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(categoriaService, times(0)).update(1L, categoriaDto);
    }

    @Test
    void updateNotFound() throws Exception {
        String localEndpoint = "/api/categorias/1";
        CategoriaDto categoriaDto = new CategoriaDto("Categoria 1", false);
        when(categoriaService.update(1L, categoriaDto)).thenThrow(new CategoriaNotFound("Categoría con id 1 no encontrada"));

        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.put(localEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(categoriaDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(categoriaService, times(1)).update(1L, categoriaDto);
    }

    @Test
    void delete() throws Exception {
        String localEndpoint = "/api/categorias/1";
        doNothing().when(categoriaService).deleteById(1L);
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.delete(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(204, response.getStatus()),
                () -> assertEquals("", response.getContentAsString())
        );
        verify(categoriaService, times(1)).deleteById(1L);
    }

    @Test
    void deleteNotFound() throws Exception {
        String localEndpoint = "/api/categorias/1";
        doThrow(new CategoriaNotFound("Categoría con id 1 no encontrada")).when(categoriaService).deleteById(1L);
        MockHttpServletResponse response = mockMvc.perform(
                        MockMvcRequestBuilders.delete(localEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(categoriaService, times(1)).deleteById(1L);
    }
}