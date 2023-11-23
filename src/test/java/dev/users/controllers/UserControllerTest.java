package dev.users.controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.rest.pedido.models.Cliente;
import dev.rest.pedido.models.Direccion;
import dev.rest.pedido.models.LineaPedido;
import dev.rest.pedido.models.Pedido;
import dev.rest.pedido.services.PedidoService;
import dev.rest.users.dto.UserInfoResponse;
import dev.rest.users.dto.UserRequest;
import dev.rest.users.dto.UserResponse;
import dev.rest.users.exceptions.UserNotFound;
import dev.rest.users.models.User;
import dev.rest.users.services.UsersService;
import dev.utils.pagination.PageResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "USER"})
public class UserControllerTest {

    private final UserRequest userRequest = UserRequest.builder()
            .nombre("User")
            .apellidos("User")
            .username("user")
            .email("user@gmail.com")
            .password("user1234")
            .build();

    private final User user = User.builder()
            .nombre("User")
            .apellidos("User")
            .password("user1234")
            .username("user")
            .email("user@gmail.com")
            .build();

    private final UserResponse userResponse = UserResponse.builder()
            .id(5L)
            .nombre("user")
            .apellidos("user")
            .username("user")
            .email("user@gmail.com")
            .build();
    private final UserInfoResponse userInfoResponse = UserInfoResponse.builder()
            .id(5L)
            .nombre("user")
            .apellidos("user")
            .username("user")
            .email("user@gmail.com")
            .build();

    private final Pedido pedido1 = Pedido.builder()
            .id(new ObjectId())
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

    private final String myEndpoint = "/api/users";
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UsersService usersService;

    @MockBean
    private PedidoService pedidoService;


    @Autowired
    public UserControllerTest(UsersService UsersService) {
        this.usersService = UsersService;
        mapper.registerModule(new JavaTimeModule());
    }


    @Test
    @WithAnonymousUser
    void NotAuthenticated() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(403, response.getStatus());
    }

    @Test
    void findAll() throws Exception {
        var list = List.of(userResponse);
        Page<UserResponse> page = new PageImpl<>(list);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

        when(usersService.findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable)).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<UserResponse> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(usersService, times(1)).findAll(Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    }

    @Test
    void findById() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";
        when(usersService.findById(anyLong(), any(Pageable.class))).thenReturn(userInfoResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        var res = mapper.readValue(response.getContentAsString(), UserInfoResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(userInfoResponse, res)
        );

        verify(usersService, times(1)).findById(anyLong(), any(Pageable.class));
    }

    @Test
    void findByIdNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        when(usersService.findById(anyLong(), any(Pageable.class))).thenThrow(new UserNotFound("No existe el usuario"));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(usersService, times(1)).findById(anyLong(), any(Pageable.class));
    }

    @Test
    void createUser() throws Exception {
        var myLocalEndpoint = myEndpoint;

        when(usersService.save(any(UserRequest.class))).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        var res = mapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(201, response.getStatus()),
                () -> assertEquals(userResponse, res)
        );

        verify(usersService, times(1)).save(any(UserRequest.class));
    }

    @Test
    void createUserBadRequest() throws Exception {
        var myLocalEndpoint = myEndpoint;

        var userRequest = UserRequest.builder()
                .nombre("user")
                .apellidos("user")
                .password("user")
                .username("user")
                .email("user@gmail.com")
                .build();
        when(usersService.save(any(UserRequest.class))).thenReturn(userResponse);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());

        verify(usersService, times(0)).save(any(UserRequest.class));
    }

    @Test
    void createUserBadRequestBlank() throws Exception {
        var myLocalEndpoint = myEndpoint;

        var userRequest = UserRequest.builder()
                .nombre("")
                .apellidos("")
                .password("user1234")
                .username("user")
                .email("")
                .build();

        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        assertAll(
                () -> assertEquals(400, response.getStatus()),
                () -> assertTrue(response.getContentAsString().contains("El nombre no puede estar")),
                () -> assertTrue(response.getContentAsString().contains("Los apellidos no puede estar"))
        );
    }


    @Test
    void updateUser() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        when(usersService.update(anyLong(), any(UserRequest.class))).thenReturn(userResponse);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        var res = mapper.readValue(response.getContentAsString(), UserResponse.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(userResponse, res)
        );

        verify(usersService, times(1)).update(anyLong(), any(UserRequest.class));
    }

    @Test
    void updateUserNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        when(usersService.update(anyLong(), any(UserRequest.class))).thenThrow(new UserNotFound("No existe el usuario"));

        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(usersService, times(1)).update(anyLong(), any(UserRequest.class));
    }

    @Test
    void deleteUser() throws Exception {

        var myLocalEndpoint = myEndpoint + "/1";

        doNothing().when(usersService).deleteById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertEquals(204, response.getStatus());

        verify(usersService, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteUserNotFound() throws Exception {
        var myLocalEndpoint = myEndpoint + "/1";

        doThrow(new UserNotFound("Usuario con ID 1 no encontrado")).when(usersService).deleteById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(404, response.getStatus());

        verify(usersService, times(1)).deleteById(anyLong());
    }

    @Test
    @WithUserDetails("admin")
    void me() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/porfile";
        when(usersService.findById(anyLong(), any(Pageable.class))).thenReturn(userInfoResponse);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertEquals(200, response.getStatus());
    }

    @Test
    @WithUserDetails("user")
    void updateMe() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/porfile";
        when(usersService.update(anyLong(), any(UserRequest.class))).thenReturn(userResponse);
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userRequest)))
                .andReturn().getResponse();
        var res = mapper.readValue(response.getContentAsString(), UserResponse.class);
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(userResponse, res)
        );
        verify(usersService, times(1)).update(anyLong(), any(UserRequest.class));
    }

    @Test
    @WithUserDetails("user")
    void deleteMe() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/porfile";
        doNothing().when(usersService).deleteById(anyLong());
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
        verify(usersService, times(1)).deleteById(anyLong());
    }

    @Test
    @WithUserDetails("user")
    void getMyPedidos() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/pedidos";
        when(pedidoService.findByIdUsuario(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(200, response.getStatus())
        );
        verify(pedidoService, times(1)).findByIdUsuario(anyLong(), any(Pageable.class));
    }

    @Test
    @WithUserDetails("user")
    void getMyPedidosById() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/pedidos/" + pedido1.get_id();
        when(pedidoService.findById(any(ObjectId.class))).thenReturn(pedido1);
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(200, response.getStatus())
        );
        verify(pedidoService, times(1)).findById(any(ObjectId.class));
    }

    @Test
    @WithUserDetails("user")
    void saveMyPedido() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/pedidos";
        when(pedidoService.save(any(Pedido.class))).thenReturn(pedido1);
        MockHttpServletResponse response = mockMvc.perform(
                        post(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(201, response.getStatus())
        );
        verify(pedidoService, times(1)).save(any(Pedido.class));

    }

    @Test
    @WithUserDetails("user")
    void updateMyPedido() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/pedidos/" + pedido1.get_id();
        when(pedidoService.update(any(ObjectId.class), any(Pedido.class))).thenReturn(pedido1);
        MockHttpServletResponse response = mockMvc.perform(
                        put(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(pedido1)))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(200, response.getStatus())
        );
        verify(pedidoService, times(1)).update(any(ObjectId.class), any(Pedido.class));
    }

    @Test
    @WithUserDetails("user")
    void deleteMyPedido() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/pedidos/" + pedido1.get_id();
        doNothing().when(pedidoService).delete(any(ObjectId.class));
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertAll(
                () -> assertEquals(204, response.getStatus())
        );
        verify(pedidoService, times(1)).delete(any(ObjectId.class));

    }


    @Test
    @WithAnonymousUser
    void meAnonymousUser() throws Exception {
        var myLocalEndpoint = myEndpoint + "/me/profile";
        MockHttpServletResponse response = mockMvc.perform(
                        get(myLocalEndpoint)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(403, response.getStatus());
    }
}



