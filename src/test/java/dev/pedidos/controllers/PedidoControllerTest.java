package dev.pedidos.controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.rest.pedido.exceptions.FunkoBadPrice;
import dev.rest.pedido.exceptions.PedidoNotFound;
import dev.rest.pedido.exceptions.PedidoNotItems;
import dev.rest.pedido.models.Cliente;
import dev.rest.pedido.models.Direccion;
import dev.rest.pedido.models.LineaPedido;
import dev.rest.pedido.models.Pedido;
import dev.rest.pedido.services.PedidoService;
import dev.utils.pagination.PageResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class PedidoControllerTest {

    private final String myEndpoint = "/api/pedidos";
    private final ObjectMapper mapper = new ObjectMapper();
    private final Pedido pedido1 = Pedido.builder()
            .id(new ObjectId("5f9f1a3b9d6b6d2e3c1d6f1a"))
            .idUsuario(1L)
            .cliente(
                    new Cliente("EvaGomez", "evagomez@soydev.dev", "1234567890",
                            new Direccion("Calle", "1", "Ciudad", "Provincia", "Pais", "12345")
                    )
            )
            .lineasPedido(List.of(LineaPedido.builder()
                    .idFunko(1L)
                    .cantidad(2)
                    .precioFunko(10.0)
                    .build()))
            .build();

    @Autowired
    MockMvc mockMvc;
    @MockBean
    private PedidoService pedidoService;

    @Autowired
    public PedidoControllerTest(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAll() throws Exception {
        var listaPedidos = List.of(pedido1);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaPedidos);

        when(pedidoService.findAll(pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Pedido> pageResponse = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, pageResponse.content().size())
        );

        verify(pedidoService, times(1)).findAll(pageable);
    }

    @Test
    void getPedidoById() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";
        when(pedidoService.findById(any(ObjectId.class))).thenReturn(pedido1);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Pedido pedido = mapper.readValue(response.getContentAsString(), Pedido.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(pedido1.getId(), pedido.getId())
        );

        verify(pedidoService, times(1)).findById(any(ObjectId.class));
    }

    @Test
    void getPedidoByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";
        when(pedidoService.findById(any(ObjectId.class)))
                .thenThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a"));
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(pedidoService, times(1)).findById(any(ObjectId.class));
    }

    @Test
    void getPedidosByUsuario() throws Exception {
        var myLocalEndpoint = myEndpoint + "/usuario/1";
        var listaPedidos = List.of(pedido1);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(listaPedidos);

        when(pedidoService.findByIdUsuario(any(Long.class), any(Pageable.class))).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Pedido> pageResponse = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, pageResponse.content().size())
        );

        verify(pedidoService, times(1)).findByIdUsuario(any(Long.class), any(Pageable.class));
    }

    @Test
    void createPedido() throws Exception {
        when(pedidoService.save(any(Pedido.class))).thenReturn(pedido1);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(pedido1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Pedido pedido = mapper.readValue(response.getContentAsString(), Pedido.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(pedido1.getId(), pedido.getId())
        );

        verify(pedidoService, times(1)).save(any(Pedido.class));
    }

    @Test
    void createPedidoBadRequest() throws Exception {
        when(pedidoService.save(any(Pedido.class))).thenThrow(new PedidoNotItems("5f9f1a3b9d6b6d2e3c1d6f1a"));
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(pedido1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );

        verify(pedidoService, times(1)).save(any(Pedido.class));
    }

    @Test
    void createPedidoBadPrice() throws Exception {
        Pedido pedido1 = Pedido.builder()
                .id(new ObjectId("5f9f1a3b9d6b6d2e3c1d6f1a"))
                .idUsuario(1L)
                .cliente(
                        new Cliente("EvaGomez", "evagomez@soydev.dev", "1234567890",
                                new Direccion("Calle", "1", "Ciudad", "Provincia", "Pais", "12345")
                        )
                )
                .lineasPedido(List.of(LineaPedido.builder()
                        .idFunko(1L)
                        .cantidad(2)
                        .precioFunko(0.0)
                        .build()))
                .build();
        when(pedidoService.save(any(Pedido.class))).thenThrow(new FunkoBadPrice(1L));
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(pedido1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(400, response.getStatus())
        );
        verify(pedidoService, times(1)).save(any(Pedido.class));
    }

    @Test
    void updatePedido() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";
        when(pedidoService.update(any(ObjectId.class), any(Pedido.class))).thenReturn(pedido1);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(pedido1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Pedido pedido = mapper.readValue(response.getContentAsString(), Pedido.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(pedido1.getId(), pedido.getId())
        );
        verify(pedidoService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }

    @Test
    void updatePedidoNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";

        when(pedidoService.update(any(ObjectId.class), any(Pedido.class)))
                .thenThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a"));

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );

        verify(pedidoService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }

    @Test
    void deletePedido() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";
        doNothing().when(pedidoService).delete(any(ObjectId.class));

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
        verify(pedidoService, times(1)).delete(any(ObjectId.class));
    }

    @Test
    void deleteNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/5f9f1a3b9d6b6d2e3c1d6f1a";
        doThrow(new PedidoNotFound("5f9f1a3b9d6b6d2e3c1d6f1a")).when(pedidoService).delete(any(ObjectId.class));
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(404, response.getStatus())
        );
        verify(pedidoService, times(1)).delete(any(ObjectId.class));
    }
}
